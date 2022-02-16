package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.utils.MultiMapUtils;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Allows {@link Verticle} instances to get simplified access to the Vert.x event bus.
 * These traits support automatic conversion of different message types like {@link JsonMessage}
 * and automatic attachment of {@link MultiMap} or {@link HeaderInformation} to sent messages on the event bus.
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * public class MyVerticle extends TelestionVerticle implements WithEventBus {
 *     @Override
 *     public void onStart() {
 *         register("channel-1", this::handle, Position.class);
 *     }
 *
 *     private void handle(Position position) {
 *         logger.info("Current position: {}, {}", position.x, position.y);
 *     }
 * }
 * }
 * </pre>
 *
 * @author Pablo Klaschka (@pklaschka), Ludwig Richter (@fussel178)
 */
public interface WithEventBus extends Verticle {

	///
	/// PUBLISH SECTION
	///

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, Object message) {
		getVertx().eventBus().publish(address, message);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, Object message, DeliveryOptions options) {
		getVertx().eventBus().publish(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, Object message, DeliveryOptions options, MultiMap... headers) {
		options.setHeaders(MultiMapUtils.merge(headers));
		publish(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, Object message, DeliveryOptions options, HeaderInformation... headers) {
		HeaderInformation.merge(headers).attach(options);
		publish(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, Object message, MultiMap... headers) {
		publish(address, message, new DeliveryOptions(), headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, Object message, HeaderInformation... headers) {
		publish(address, message, new DeliveryOptions(), headers);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, JsonMessage message) {
		publish(address, message.json());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, JsonMessage message, DeliveryOptions options) {
		publish(address, message.json(), options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, JsonMessage message, DeliveryOptions options, MultiMap... headers) {
		publish(address, message.json(), options, headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, JsonMessage message, DeliveryOptions options, HeaderInformation... headers) {
		publish(address, message.json(), options, headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, JsonMessage message, MultiMap... headers) {
		publish(address, message.json(), headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, JsonMessage message, HeaderInformation... headers) {
		publish(address, message.json(), headers);
	}

	///
	/// SEND SECTION
	///

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, Object message) {
		getVertx().eventBus().send(address, message);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, Object message, DeliveryOptions options) {
		getVertx().eventBus().send(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, Object message, DeliveryOptions options, MultiMap... headers) {
		options.setHeaders(MultiMapUtils.merge(headers));
		send(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, Object message, DeliveryOptions options, HeaderInformation... headers) {
		HeaderInformation.merge(headers).attach(options);
		send(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, Object message, MultiMap... headers) {
		send(address, message, new DeliveryOptions(), headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, Object message, HeaderInformation... headers) {
		send(address, message, new DeliveryOptions(), headers);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonMessage message) {
		send(address, message.json());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, JsonMessage message, DeliveryOptions options) {
		send(address, message.json(), options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, JsonMessage message, DeliveryOptions options, MultiMap... headers) {
		send(address, message.json(), options, headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, JsonMessage message, DeliveryOptions options, HeaderInformation... headers) {
		send(address, message.json(), options, headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonMessage message, MultiMap... headers) {
		send(address, message.json(), headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonMessage message, HeaderInformation... headers) {
		send(address, message.json(), headers);
	}

	///
	/// REQUEST SECTION
	///

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, Object request) {
		return getVertx().eventBus().request(address, request);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(String address, Object request, DeliveryOptions options) {
		return getVertx().eventBus().request(address, request, options);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(
			String address,
			Object request,
			DeliveryOptions options,
			MultiMap... requestHeaders) {
		options.setHeaders(MultiMapUtils.merge(requestHeaders));
		return request(address, request, options);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(
			String address,
			Object request,
			DeliveryOptions options,
			HeaderInformation... requestHeaders) {
		HeaderInformation.merge(requestHeaders).attach(options);
		return request(address, request, options);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, Object request, MultiMap... requestHeaders) {
		return request(address, request, new DeliveryOptions(), requestHeaders);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, Object request, HeaderInformation... requestHeaders) {
		return request(address, request, new DeliveryOptions(), requestHeaders);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, JsonMessage message) {
		return request(address, message.json());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(String address, JsonMessage message, DeliveryOptions options) {
		return request(address, message.json(), options);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(
			String address,
			JsonMessage message,
			DeliveryOptions options,
			MultiMap... requestHeaders) {
		return request(address, message.json(), options, requestHeaders);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(
			String address,
			JsonMessage message,
			DeliveryOptions options,
			HeaderInformation... requestHeaders) {
		return request(address, message.json(), options, requestHeaders);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, JsonMessage message, MultiMap... requestHeaders) {
		return request(address, message.json(), requestHeaders);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, JsonMessage message, HeaderInformation... requestHeaders) {
		return request(address, message.json(), requestHeaders);
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType) {
		return DecodedMessage.compose(responseType, request(address, request));
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType,
			DeliveryOptions options) {
		return DecodedMessage.compose(responseType, request(address, request, options));
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType,
			DeliveryOptions options,
			MultiMap... requestHeaders) {
		return DecodedMessage.compose(responseType, request(address, request, options, requestHeaders));
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType,
			DeliveryOptions options,
			HeaderInformation... requestHeaders) {
		return DecodedMessage.compose(responseType, request(address, request, options, requestHeaders));
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType,
			MultiMap... requestHeaders) {
		return DecodedMessage.compose(responseType, request(address, request, requestHeaders));
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType,
			HeaderInformation... requestHeaders) {
		return DecodedMessage.compose(responseType, request(address, request, requestHeaders));
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonMessage request,
			Class<V> responseType) {
		return request(address, request.json(), responseType);
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonMessage request,
			Class<V> responseType,
			DeliveryOptions options) {
		return request(address, request.json(), responseType, options);
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonMessage request,
			Class<V> responseType,
			DeliveryOptions options,
			MultiMap... requestHeaders) {
		return request(address, request.json(), responseType, options, requestHeaders);
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonMessage request,
			Class<V> responseType,
			DeliveryOptions options,
			HeaderInformation... requestHeaders) {
		return request(address, request.json(), responseType, options, requestHeaders);
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonMessage request,
			Class<V> responseType,
			MultiMap... requestHeaders) {
		return request(address, request.json(), responseType, requestHeaders);
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonMessage request,
			Class<V> responseType,
			HeaderInformation... requestHeaders) {
		return request(address, request.json(), responseType, requestHeaders);
	}

	///
	/// REGISTER SECTION
	///

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
	default <V extends JsonMessage, T> void register(
			String address,
			ExtendedMessageHandler<V, T> handler,
			Class<V> type) {
		this.<T>register(address, message -> JsonMessage.on(type, message, body -> handler.handle(body, message)));
	}
}
