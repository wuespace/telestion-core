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
	/* PUBLISH */

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

	/* SEND */

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

	/* REQUEST */

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(String address, Object request, DeliveryOptions options) {
		return getVertx().eventBus().request(address, request, options);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, Object request) {
		return getVertx().eventBus().request(address, request);
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address, Object request, DeliveryOptions options, Class<V> responseType
	) {
		return this.<T>request(address, request, options)
				.map(raw -> new DecodedMessage<>(raw.body().mapTo(responseType), raw));
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address, Object request, Class<V> responseType
	) {
		return this.<T>request(address, request)
				.map(raw -> new DecodedMessage<>(raw.body().mapTo(responseType), raw));
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address, JsonMessage request, DeliveryOptions options, Class<V> responseType
	) {
		return request(address, request.json(), options, responseType);
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address, JsonMessage request, Class<V> responseType
	) {
		return request(address, request.json(), responseType);
	}

	/* REGISTER/CONSUME */

	/**
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 */
	default <T> void register(String address, Handler<Message<T>> handler) {
		getVertx().eventBus().consumer(address, handler);
	}

	/**
	 * @param type the type of received message to map to
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 */
	default <V extends JsonMessage> void register(String address, MessageHandler<V> handler, Class<V> type) {
		register(address, message -> JsonMessage.on(type, message, handler::handle));
	}

	/**
	 * @param type the type of received message to map to
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 */
	default <V extends JsonMessage, T> void register(String address, ExtendedMessageHandler<V, T> handler, Class<V> type) {
		this.<T>register(address, message -> JsonMessage.on(type, message, body -> handler.handle(body, message)));
	}
}
