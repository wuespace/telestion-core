package de.wuespace.telestion.api.verticle.trait;

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
	default void publish(String address, Object message, DeliveryOptions options) {
		getVertx().eventBus().publish(address, message, options);
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
	default void publish(String address, Object message) {
		getVertx().eventBus().publish(address, message);
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
	default void send(String address, Object message, DeliveryOptions options) {
		getVertx().eventBus().send(address, message, options);
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
	default void send(String address, Object message) {
		getVertx().eventBus().send(address, message);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonMessage message) {
		send(address, message.json());
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T extends JsonMessage> Future<DecodedMessage<T>> request(
			String address, Object request, DeliveryOptions options, Class<T> responseType) {
		return getVertx().eventBus().request(address, request, options)
				.map(raw -> new DecodedMessage<>(JsonMessage.from(raw.body(), responseType), raw));
	}

	default <T extends JsonMessage> Future<DecodedMessage<T>> request(
			String address, JsonMessage request, DeliveryOptions options, Class<T> responseType) {
		return request(address, request.json(), options, responseType);
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T extends JsonMessage> Future<DecodedMessage<T>> request(
			String address, Object request, Class<T> responseType) {
		return getVertx().eventBus().request(address, request)
				.map(raw -> new DecodedMessage<>(((JsonObject) raw.body()).mapTo(responseType), raw));
	}

	default <T extends JsonMessage> Future<DecodedMessage<T>> request(
			String address, JsonMessage request, Class<T> responseType) {
		return request(address, request.json(), responseType);
	}

	/**
	 * Registers a handler onto an eventbus channel.
	 *
	 * @param address the eventbus address name
	 * @param handler the handler that gets called when a new message arrives at the specified eventbus address
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 */
	default void register(String address, Handler<Message<Object>> handler) {
		getVertx().eventBus().consumer(address, handler);
	}

	/**
	 * Registers a handler onto an eventbus channel.
	 * The received messages are mapped to the specified json type before handing over to the handler.
	 *
	 * @param address the eventbus address name
	 * @param handler the handler that gets called when a new message arrives at the specified eventbus address
	 * @param type    the json type to map to
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 */
	default <T extends JsonMessage> void register(String address, MessageHandler<T> handler, Class<T> type) {
		getVertx().eventBus().consumer(address, message -> JsonMessage.on(type, message, handler::handle));
	}

	/**
	 * Registers a handler onto an eventbus channel.
	 * The received messages are mapped to the specified json type before handing over to the handler.
	 * The handler gets the plain message object received from the eventbus, too.
	 *
	 * @param address the eventbus address name
	 * @param handler the handler that gets called when a new message arrives at the specified eventbus address
	 * @param type    the json type to map to
	 */
	default <T extends JsonMessage> void register(String address, ExtendedMessageHandler<T> handler, Class<T> type) {
		getVertx().eventBus().consumer(address,
				message -> JsonMessage.on(type, message,
						body -> handler.handle(body, message)));
	}
}