package de.wuespace.telestion.services.connection.rework.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.connection.rework.ConnectionData;
import de.wuespace.telestion.services.connection.rework.Tuple;
import io.reactivex.annotations.NonNull;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * <strong>An implementation of an unencrypted TCP-Server.</strong>
 * </p>
 *
 * <p>
 * Features are:
 * <ul>
 *     <li>Opening new connections to TCP-Clients</li>
 *     <li>Receiving data from all the open connections</li>
 *     <li>Keep the connections open until either no package gets sent for a certain amount of time (timeout) or
 *         the Client closes the connection</li>
 *     <li>Sending answers back to the Client if the connections are still open</li>
 * </ul>
 */
public final class TcpServer extends AbstractVerticle {

	/**
	 * Configuration for this Verticle which can be loaded from a config.<br>
	 * An optional timeout can be specified which is the consecutive time without any packets incoming or outgoing after
	 * which a client will be disconnected. Special timeouts can be found in {@link TcpTimeouts}.
	 *
	 * @param inAddress     address on which the verticle listens on
	 * @param outAddress    address on which the verticle publishes
	 * @param host          host on which the Server-Socket should run
	 * @param port          port on which the Server-Socket should run
	 * @param clientTimeout time until timeout
	 */
	public final record Configuration(@JsonProperty String inAddress,
									  @JsonProperty String outAddress,
									  @JsonProperty String host,
									  @JsonProperty int port,
									  @JsonProperty long clientTimeout) implements JsonMessage {
		private Configuration() {
			this(null, null, "0.0.0.0", 0, TcpTimeouts.DEFAULT_TIMEOUT);
		}

		public Configuration(@NonNull String inAddress, @NonNull String outAddress, @NonNull String host, int port) {
			this(inAddress, outAddress, host, port, TcpTimeouts.DEFAULT_TIMEOUT);
		}
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		config = Config.get(config, new Configuration(), config(), Configuration.class);

		// Unencrypted -> TODO: Implement functionality for encryption
		var serverOptions = new NetServerOptions();
		serverOptions.setHost(config.host());
		serverOptions.setPort(config.port());
		serverOptions.setIdleTimeout(config.clientTimeout() == TcpTimeouts.NO_RESPONSES
				? (int) TcpTimeouts.NO_TIMEOUT : (int) config.clientTimeout());
		serverOptions.setIdleTimeoutUnit(TimeUnit.MILLISECONDS);

		activeCons = new HashMap<>();
		server = vertx.createNetServer(serverOptions);

		// setup
		server.connectHandler(this::onConnected);
		server.exceptionHandler(error ->
				logger.error("Encountered an unexpected error (Config: {})", config.json(), error));

		server.listen(handler -> {
			if (handler.failed()) {
				logger.warn("Could not start server on {}:{}", config.host(), config.port());
				startPromise.fail(handler.cause());
				return;
			}

			var port = handler.result().actualPort();
			logger.info("Successfully started. Running on {}:{}", config.host(), port);
			startPromise.complete();
		});

		vertx.eventBus().consumer(config.inAddress, raw -> {
			if (!JsonMessage.on(TcpData.class, raw, this::handleDispatchedMsg)) {
				JsonMessage.on(ConnectionData.class, raw, this::handleMsg);
			}
		});
	}

	@Override
	public void stop(Promise<Void> stopPromise) throws Exception {
		if (server == null) {
			stopPromise.complete();
			return;
		}

		logger.info("Closing Server on {}:{}", config.host(), config.port());
		server.close(handler -> {
			if (handler.failed()) {
				logger.warn("Cannot close server on {}:{}", config.host(), config.port());
				stopPromise.fail(handler.cause());
				return;
			}

			stopPromise.complete();
		});
	}

	public TcpServer() {
		this(null);
	}

	public TcpServer(Configuration config) {
		this.config = config;
	}

	public Configuration getConfig() {
		return this.config;
	}

	// Public for own Dispatcher implementations
	public boolean isActiveCon(Tuple<String, Integer> key) {
		return activeCons.containsKey(key);
	}

	private void onConnected(NetSocket netSocket) {
		var ip = netSocket.remoteAddress().hostAddress();
		var port = netSocket.remoteAddress().port();
		var key = new Tuple<>(ip, port);

		logger.info("Connection established with {}:{}", ip, port);
		activeCons.put(key, netSocket);

		var packetIdCounter = new AtomicInteger(0);

		netSocket.handler(buffer -> {
			var packetId = packetIdCounter.getAndIncrement();
			logger.debug("New message received from Client ({}:{}, packetId={})", ip, port, packetId);
			vertx.eventBus().publish(config.outAddress(), new ConnectionData(buffer.getBytes(), new TcpDetails(ip, port,
					packetId)).json());
			if (config.clientTimeout() == TcpTimeouts.NO_RESPONSES) {
				netSocket.close();
			}
		});

		netSocket.exceptionHandler(error -> {
			logger.error("Encountered an unexpected error (Client: {}:{})", ip, port,
					error);
			activeCons.remove(key);
		});

		netSocket.closeHandler(handler -> {
			logger.info("Closing remote connection with {}:{}", ip, port);
			activeCons.remove(key);
		});
	}

	private void handleDispatchedMsg(TcpData data) {
		var details = data.details();

		var element = activeCons.get(new Tuple<>(details.ip(), details.port()));

		// Might be useful to send this to the TCP-Client however the config must be varied for this (future update?)
		if (element == null) {
			logger.warn("Requested connection {}:{} is (no longer) available. Packet will be dropped.",
					data.details().ip(), data.details().port());
			return;
		}

		logger.debug("Sending message to {}:{}", details.ip(), details.port());

		if (element.writeQueueFull()) {
			logger.error("Write queue of socket is full ({}:{}). Packet will be dropped.", details.ip(),
					details.port());
			return;
		}

		element.write(Buffer.buffer(data.data()));
	}

	private void handleMsg(ConnectionData data) {
		var details = data.conDetails();
		if (details instanceof TcpDetails tcpDetails) {
			handleDispatchedMsg(new TcpData(data.rawData(), tcpDetails));
		} else {    // Shouldn't happen due to Dispatcher
			logger.warn("Wrong connection detail type received. Packet will be dropped.");
			// If there will be ever a logger for broken packets, send this
		}
	}

	private Configuration config;
	private NetServer server;
	private Map<Tuple<String, Integer>, NetSocket> activeCons;

	private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
}
