package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;

/**
 * An event handler which accepts the decoded body of the message
 * and the message itself and returns nothing.
 *
 * @author Ludwig Richter (@fussel178)
 * @see WithEventBus#register(String, ExtendedMessageHandler, Class)
 */
@FunctionalInterface
public interface ExtendedMessageHandler<V extends JsonMessage, T> {
	void handle(V body, Message<T> message);
}
