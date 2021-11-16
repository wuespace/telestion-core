package de.wuespace.telestion.services.connection.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.connection.Broadcaster;
import de.wuespace.telestion.services.connection.ConnectionData;
import de.wuespace.telestion.services.connection.IpDetails;
import de.wuespace.telestion.services.connection.RawMessage;
import io.reactivex.annotations.NonNull;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class TcpClient extends BaseTcpVerticle {

	public final record Configuration(@JsonProperty String inAddress,
									  @JsonProperty String outAddress,
									  @JsonProperty long timeout,
									  @JsonProperty int broadcasterId) implements JsonMessage {
		public Configuration() {
			this(null, null, TcpTimeouts.DEFAULT_TIMEOUT, Broadcaster.DEFAULT_ID);
		}
	}

	@Override
	public void start(Promise<Void> startPromise) {
		config = Config.get(config, new Configuration(), config(), Configuration.class);

		var options = new NetClientOptions();
		options.setIdleTimeout(config.timeout() == TcpTimeouts.NO_RESPONSES
				? (int) TcpTimeouts.NO_TIMEOUT : (int) config.timeout());

		currentClient = vertx.createNetClient(options);
		activeClients = new HashMap<>();

		Broadcaster.register(config.broadcasterId(), config.inAddress());

		vertx.eventBus().consumer(config.inAddress(), handler -> this.consumer(handler, activeClients));
		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		var composite = CompositeFuture.all(
				activeClients.values().stream().map(NetSocket::close).collect(Collectors.toList())
		);

		composite.onComplete(result -> {
			if (result.failed()) {
				stopPromise.fail(result.cause());
				return;
			}

			stopPromise.complete();
		});
	}

	public TcpClient() {
		this(null);
	}

	public TcpClient(Configuration config) {
		this.config = config;
	}

	protected void handleMsg(ConnectionData data) {
		var details = data.conDetails();
		if (details instanceof TcpDetails tcpDet) {
			handleDispatchedMsg(new TcpData(data.rawData(), tcpDet));
		} else {    // Shouldn't happen due to Dispatcher
			logger.warn("Wrong connection detail type received. Packet will be dropped.");
			// If there will ever be a logger for broken packets, send this to it
		}
	}

	protected void handleDispatchedMsg(TcpData tcpData) {
		var details = tcpData.details();
		var key = new IpDetails(details.ip(), details.port());

		// Checks if socket already exists.
		// If not, connects to Server and if successful sends data asynchronously.
		vertx.executeBlocking(promise -> {
			if (activeClients.containsKey(key)) {
				promise.complete();
				return;
			}

			currentClient.connect(details.port(), details.ip(), result -> {
				if (result.succeeded()) {
					onConnected(result.result());
					promise.complete();
					return;
				}

				logger.error("Failed to create new NetClient with {}:{}:",
						details.ip(), details.port(), result.cause());
				promise.fail(result.cause());
			});
		}, result -> {
			if (result.failed()) {
				logger.warn("Due to an error the packet to {}:{} will be dropped", details.ip(), details.port());
				return;
			}

			logger.debug("Sending data to {}:{}", details.ip(), details.port());
			// TcpClient.write() is non-blocking and returns a Future which theoretically could be used to monitor the
			// success of this process if needed in the future. (maybe inform if dropped?)
			activeClients
					.get(new IpDetails(details.ip(), details.port()))
					.write(Buffer.buffer(tcpData.data()));
		});
	}

	/**
	 * Called when a new socket is opened.
	 *
	 * @param socket the newly connected socket
	 */
	private void onConnected(NetSocket socket) {
		var ip = socket.remoteAddress().host();
		var port = socket.remoteAddress().port();
		var key = new IpDetails(ip, port);

		activeClients.put(key, socket);
		logger.info("Connection established ({}:{})", ip, port);

		var packetIdCounter = new AtomicInteger(0);

		socket.handler(buffer -> {
			var packetId = packetIdCounter.getAndIncrement();
			logger.debug("New message received from Server ({}:{}, packetId={})", ip, port, packetId);
			vertx.eventBus().publish(
					config.outAddress(),
					new ConnectionData(buffer.getBytes(), new TcpDetails(ip, port, packetId)).json()
			);
		});

		socket.exceptionHandler(error -> {
			logger.error("Encountered an unexpected error (Server: {}:{})", ip, port, error);
			activeClients.remove(key);
		});

		socket.closeHandler(handler -> {
			logger.info("Closing remote connection with Server ({}:{})", ip, port);
			activeClients.remove(key);
		});
	}

	private Configuration config;
	private Map<IpDetails, NetSocket> activeClients;
	private NetClient currentClient;

	private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);
}
