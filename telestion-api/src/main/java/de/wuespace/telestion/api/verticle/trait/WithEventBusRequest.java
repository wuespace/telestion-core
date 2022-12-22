package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.message.JsonRecord;
import de.wuespace.telestion.api.message.MultiMapUtils;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * See {@link WithEventBus} (but only {@code request()} methods)
 */
public interface WithEventBusRequest extends Verticle {
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
}
