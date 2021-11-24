package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.JsonMessage;

@FunctionalInterface
public interface MessageHandler<T extends JsonMessage> {
	void handle(T body);
}
