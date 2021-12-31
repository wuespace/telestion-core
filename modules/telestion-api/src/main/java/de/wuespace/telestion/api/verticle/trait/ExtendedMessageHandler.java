package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;

@FunctionalInterface
public interface ExtendedMessageHandler<V extends JsonMessage, T> {
	void handle(V body, Message<T> message);
}
