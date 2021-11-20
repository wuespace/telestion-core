package de.wuespace.telestion.api.traits;

import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;

@FunctionalInterface
public interface ExtendedMessageHandler<T extends JsonMessage> {
	void handle(T body, Message<Object> message);
}
