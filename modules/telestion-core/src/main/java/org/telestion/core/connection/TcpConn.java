package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.config.Config;
import org.telestion.core.message.Address;

import java.util.Collections;
import java.util.List;

/**
 * This class opens a tcp connection. This could either be a tcp client to a host or a host which accepts new clients.
 * It is configured with the file based config pattern. After a connection is established the {@link Participant} is
 * published on the event bus. Incoming messages are published to the event bus too. The connection listens to the event
 * bus and send incoming messages over tcp to the participant. All addresses are defined in the configuration.
 */
public final class TcpConn extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(TcpConn.class);
	private final Configuration forcedConfig;
	private Configuration config;
	private NetServer server;
	private NetClient client;

	/**
	 * Create a {@link TcpConn} either with file based or default configuration.
	 */
	public TcpConn() {
		forcedConfig = null;
	}

	/**
	 * Create a {@link TcpConn} with forced configuration.
	 *
	 * @param host               the host to which the connection should be established or null if this is the host of
	 *                           the connection
	 * @param port               the port of the connection host
	 * @param broadcastAddress   the address to which the incoming data should be published or null if no publishing is
	 *                           allowed
	 * @param targetAddresses    the list of addresses to which the incoming data should be send or null if no direct
	 *                           targets exist
	 * @param consumingAddresses the list of addresses from which data will be consumed
	 */
	public TcpConn(String host, int port, String broadcastAddress, List<String> targetAddresses,
			List<String> consumingAddresses) {
		forcedConfig = new Configuration(host, port, broadcastAddress, targetAddresses, consumingAddresses);
	}

	/**
	 * Completes a promise based on the success of a {@link AsyncResult}. If it was successful a handler will be called.
	 *
	 * @param result  the result which is observed
	 * @param promise the promise which will be completed
	 * @param handler the handler which will be executed on a successful result
	 * @param <T>     the type of the result
	 */
	private static <T> void complete(AsyncResult<T> result, Promise<?> promise, Handler<T> handler) {
		if (result.failed()) {
			promise.fail(result.cause());
			return;
		}
		handler.handle(result.result());
		promise.tryComplete();
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		config = Config.get(forcedConfig, config(), Configuration.class);

		if (config.host == null) {
			server = vertx.createNetServer(new NetServerOptions().setPort(config.port));
			server.connectHandler(this::onConnected);
			server.exceptionHandler(handler -> logger.error("TCP-Server encountered an unexpected error", handler));
			server.listen(h -> complete(h, startPromise,
					r -> logger.info("TCP-Server successfully started. Running on port {}", r.actualPort())));
		} else {
			client = vertx.createNetClient(new NetClientOptions());
			client.connect(config.port, config.host, h -> complete(h, startPromise, this::onConnected));
		}
	}

	@Override
	public void stop(Promise<Void> stopPromise) throws Exception {
		if (server != null) {
			server.close(stopPromise);
			return;
		}
		if (client != null) {
			client.close();
			stopPromise.complete();
			return;
		}
		stopPromise.complete();
	}

	/**
	 * Called when a new socket is opened. It sets up the messaging logic and broadcasts a message with the
	 * {@link Participant} to the consumers.
	 *
	 * @param socket the newly connected socket
	 */
	private void onConnected(NetSocket socket) {
		var remoteHost = socket.remoteAddress().host();
		var remotePort = socket.remoteAddress().port();
		var participant = new Participant(remoteHost, remotePort);
		logger.info("Connection established ({})", participant);

		socket.handler(buffer -> out(new Data(participant, buffer.getBytes()).json()));
		socket.closeHandler(handler -> logger.info("Connection closed ({})", socket.remoteAddress()));

		consume(msg -> JsonMessage.on(Data.class, msg, data -> {
			if (!participant.equals(data.participant())) {
				return;
			}
			if (socket.writeQueueFull()) {
				logger.error("Write queue of socket is full addr={}, port={}", remoteHost, remotePort);
				return;
			}
			socket.write(Buffer.buffer(data.data()));
		}));

		out(participant.json());
	}

	/**
	 * Registers handler to all consuming addresses specified in the configuration.
	 *
	 * @param handler the actual handler
	 * @param <T>     the type of the handled object
	 */
	private <T> void consume(Handler<Message<T>> handler) {
		if (config.consumingAddresses() != null) {
			config.consumingAddresses().forEach(addr -> {
				vertx.eventBus().consumer(addr, handler);
			});
		}
	}

	/**
	 * Publishes data to the addresses which are specified in the configuration.
	 *
	 * @param data the actual data
	 */
	private void out(Object data) {
		if (config.broadcastAddress() != null) {
			vertx.eventBus().publish(config.broadcastAddress(), data);
		}
		if (config.targetAddresses() != null) {
			config.targetAddresses().forEach(addr -> vertx.eventBus().send(addr, data));
		}
	}

	/**
	 * A chunk of data which is transmitted with the {@link TcpConn}.
	 *
	 * @param participant the participant of the tcp connection which has send this chunk of data or should receive it
	 * @param data        the actual data
	 */
	@SuppressWarnings("preview") public static record Data(@JsonProperty Participant participant,
			@JsonProperty byte[] data) implements JsonMessage {

		@SuppressWarnings("unused")
		private Data() {
			this(null, null);
		}
	}

	/**
	 * A participant of the {@link TcpConn}
	 *
	 * @param host its host address
	 * @param port its port
	 */
	@SuppressWarnings("preview") public static record Participant(@JsonProperty String host, @JsonProperty int port)
			implements JsonMessage {

		@SuppressWarnings("unused")
		private Participant() {
			this(null, 0);
		}
	}

	/**
	 * @param host               the host to which the connection should be established or null if this is the host of
	 *                           the connection
	 * @param port               the port of the connection host
	 * @param broadcastAddress   the address to which the incoming data should be published or null if no publishing is
	 *                           allowed
	 * @param targetAddresses    the list of addresses to which the incoming data should be send or null if no direct
	 *                           targets exist
	 * @param consumingAddresses the list of addresses from which data will be consumed
	 */
	@SuppressWarnings("preview") private static record Configuration(@JsonProperty String host, @JsonProperty int port,
			@JsonProperty String broadcastAddress, @JsonProperty List<String> targetAddresses,
			@JsonProperty List<String> consumingAddresses) {
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, 7777, Address.outgoing(TcpConn.class), null,
					Collections.singletonList(Address.incoming(TcpConn.class)));
		}
	}
}
