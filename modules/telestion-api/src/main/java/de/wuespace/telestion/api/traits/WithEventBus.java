package de.wuespace.telestion.api.traits;

import de.wuespace.telestion.api.Tuple;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Allows {@link Verticle} instances to get simplified access to the Vert.x event bus.
 * All methods allow usage with {@link JsonMessage} messages, too.
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * public class MyVerticle extends TelestionVerticle implements WithEventBus {
 *     @Override
 *     public void startVerticle() {
 *         register("channel-1", this::handle, SimpleMessage.class);
 *     }
 *
 *     private void handle(SimpleMessage body) {
 *         logger.info("Received Telecommand: {}", body.tcClass());
 *     }
 * }
 * }
 * </pre>
 *
 * @author Pablo Klaschka, Ludwig Richter
 */
public interface WithEventBus extends Verticle {
	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, JsonObject message, DeliveryOptions options) {
		getVertx().eventBus().publish(address, message, options);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, JsonObject message) {
		getVertx().eventBus().publish(address, message);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, JsonMessage message, DeliveryOptions options) {
		publish(address, message.json(), options);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, JsonMessage message) {
		publish(address, message.json());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, JsonObject message, DeliveryOptions options) {
		getVertx().eventBus().send(address, message, options);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonObject message) {
		getVertx().eventBus().send(address, message);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, JsonMessage message, DeliveryOptions options) {
		send(address, message.json(), options);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonMessage message) {
		send(address, message.json());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 * @param responseType the type of the response to map to
	 */
	default <T extends JsonMessage> Future<Tuple<T, Message<Object>>> request(
			String address, JsonObject message, DeliveryOptions options, Class<T> responseType) {
		return getVertx().eventBus().request(address, message, options)
				.map(raw -> new Tuple<>(JsonMessage.from(raw.body(), responseType), raw));
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 * @param responseType the type of the response to map to
	 */
	default <T extends JsonMessage> Future<Tuple<T, Message<Object>>> request(
			String address, JsonObject message, Class<T> responseType) {
		return getVertx().eventBus().request(address, message)
				.map(raw -> new Tuple<>(JsonMessage.from(raw.body(), responseType), raw));
	}

	/**
	 * Registers a handler onto an eventbus channel.
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 * @param address the eventbus address name
	 * @param handler the handler that gets called when a new message arrives at the specified eventbus address
	 */
	default void register(String address, Handler<Message<Object>> handler) {
		getVertx().eventBus().consumer(address, handler);
	}

	/**
	 * Registers a handler onto an eventbus channel.
	 * The received messages are mapped to the specified json type before handing over to the handler.
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 * @param address the eventbus address name
	 * @param handler the handler that gets called when a new message arrives at the specified eventbus address
	 * @param type the json type to map to
	 */
	default <T extends JsonMessage> void register(String address, MessageHandler<T> handler, Class<T> type) {
		getVertx().eventBus().consumer(address, message -> JsonMessage.on(type, message, handler::handle));
	}

	/**
	 * Registers a handler onto an eventbus channel.
	 * The received messages are mapped to the specified json type before handing over to the handler.
	 * The handler gets the plain message object received from the eventbus, too.
	 * @param address the eventbus address name
	 * @param handler the handler that gets called when a new message arrives at the specified eventbus address
	 * @param type the json type to map to
	 */
	default <T extends JsonMessage> void register(String address, ExtendedMessageHandler<T> handler, Class<T> type) {
		getVertx().eventBus().consumer(address,
				message -> JsonMessage.on(type, message,
						body -> handler.handle(body, message)));
	}
}
