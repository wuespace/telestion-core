package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.JsonRecord;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.Message;

/**
 * See {@link WithEventBus} (but only {@code register()} methods)
 */
public interface WithEventBusRegister extends Verticle {
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
