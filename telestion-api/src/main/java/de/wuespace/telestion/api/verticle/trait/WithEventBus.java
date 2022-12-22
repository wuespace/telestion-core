package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.message.JsonRecord;
import de.wuespace.telestion.api.message.MultiMapUtils;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Allows {@link Verticle} instances to get simplified access to the Vert.x event bus.
 * These traits support automatic conversion of different message types like {@link JsonRecord}
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

	///////////////////////////////////////////////////////////////////////////
	// publish section
	///////////////////////////////////////////////////////////////////////////

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
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, Object message, DeliveryOptions options, MultiMap... headers) {
		options.setHeaders(MultiMapUtils.merge(headers));
		publish(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, Object message, MultiMap... headers) {
		publish(address, message, new DeliveryOptions(), headers);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, JsonRecord message) {
		publish(address, message.toJsonObject());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, JsonRecord message, DeliveryOptions options) {
		publish(address, message.toJsonObject(), options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object, DeliveryOptions)
	 */
	default void publish(String address, JsonRecord message, DeliveryOptions options, MultiMap... headers) {
		publish(address, message.toJsonObject(), options, headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#publish(String, Object)
	 */
	default void publish(String address, JsonRecord message, MultiMap... headers) {
		publish(address, message.toJsonObject(), headers);
	}

	///////////////////////////////////////////////////////////////////////////
	// send section
	///////////////////////////////////////////////////////////////////////////

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
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, Object message, DeliveryOptions options, MultiMap... headers) {
		options.setHeaders(MultiMapUtils.merge(headers));
		send(address, message, options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, Object message, MultiMap... headers) {
		send(address, message, new DeliveryOptions(), headers);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonRecord message) {
		send(address, message.toJsonObject());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, JsonRecord message, DeliveryOptions options) {
		send(address, message.toJsonObject(), options);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object, DeliveryOptions)
	 */
	default void send(String address, JsonRecord message, DeliveryOptions options, MultiMap... headers) {
		send(address, message.toJsonObject(), options, headers);
	}

	/**
	 * @param headers the headers that should be sent with the message
	 *                (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#send(String, Object)
	 */
	default void send(String address, JsonRecord message, MultiMap... headers) {
		send(address, message.toJsonObject(), headers);
	}

	///////////////////////////////////////////////////////////////////////////
	// request section
	///////////////////////////////////////////////////////////////////////////

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
	 *                       (will usually be a {@link HeaderInformation} object)
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
	 *                       (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, Object request, MultiMap... requestHeaders) {
		return request(address, request, new DeliveryOptions(), requestHeaders);
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, JsonRecord message) {
		return request(address, message.toJsonObject());
	}

	/**
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(String address, JsonRecord message, DeliveryOptions options) {
		return request(address, message.toJsonObject(), options);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 *                       (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <T> Future<Message<T>> request(
			String address,
			JsonRecord message,
			DeliveryOptions options,
			MultiMap... requestHeaders) {
		return request(address, message.toJsonObject(), options, requestHeaders);
	}

	/**
	 * @param requestHeaders the headers that should be sent with the request message
	 *                       (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <T> Future<Message<T>> request(String address, JsonRecord message, MultiMap... requestHeaders) {
		return request(address, message.toJsonObject(), requestHeaders);
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType) {
		return DecodedMessage.compose(responseType, request(address, request));
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType,
			DeliveryOptions options) {
		return DecodedMessage.compose(responseType, request(address, request, options));
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 *                       (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
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
	 *                       (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			Object request,
			Class<V> responseType,
			MultiMap... requestHeaders) {
		return DecodedMessage.compose(responseType, request(address, request, requestHeaders));
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonRecord request,
			Class<V> responseType) {
		return request(address, request.toJsonObject(), responseType);
	}

	/**
	 * @param responseType the type of the response to map to
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonRecord request,
			Class<V> responseType,
			DeliveryOptions options) {
		return request(address, request.toJsonObject(), responseType, options);
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 *                       (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object, DeliveryOptions)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonRecord request,
			Class<V> responseType,
			DeliveryOptions options,
			MultiMap... requestHeaders) {
		return request(address, request.toJsonObject(), responseType, options, requestHeaders);
	}

	/**
	 * @param responseType   the type of the response to map to
	 * @param requestHeaders the headers that should be sent with the request message
	 *                       (will usually be a {@link HeaderInformation} object)
	 * @see io.vertx.core.eventbus.EventBus#request(String, Object)
	 */
	default <V extends JsonRecord, T extends JsonObject> Future<DecodedMessage<V, T>> request(
			String address,
			JsonRecord request,
			Class<V> responseType,
			MultiMap... requestHeaders) {
		return request(address, request.toJsonObject(), responseType, requestHeaders);
	}

	///////////////////////////////////////////////////////////////////////////
	// register section
	///////////////////////////////////////////////////////////////////////////

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
	default <V extends JsonRecord> void register(String address, MessageHandler<V> handler, Class<V> type) {
		register(address, message -> JsonRecord.on(type, message, handler::handle));
	}

	/**
	 * @param type the type of received message to map to
	 * @see io.vertx.core.eventbus.EventBus#consumer(String, Handler)
	 */
	default <V extends JsonRecord, T> void register(
			String address,
			ExtendedMessageHandler<V, T> handler,
			Class<V> type) {
		this.<T>register(address, message -> JsonRecord.on(type, message, body -> handler.handle(body, message)));
	}
}
