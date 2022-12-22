package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.message.JsonRecord;
import de.wuespace.telestion.api.message.MultiMapUtils;
import io.vertx.core.MultiMap;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;

/**
 * See {@link WithEventBus} (but only {@code publish()} methods)
 */
public interface WithEventBusPublish extends Verticle {
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
}
