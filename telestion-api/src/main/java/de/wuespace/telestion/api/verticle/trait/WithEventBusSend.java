package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.message.JsonRecord;
import de.wuespace.telestion.api.message.MultiMapUtils;
import io.vertx.core.MultiMap;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;

/**
 * See {@link WithEventBus} (but only {@code send()} methods)
 */
public interface WithEventBusSend extends Verticle {
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
}
