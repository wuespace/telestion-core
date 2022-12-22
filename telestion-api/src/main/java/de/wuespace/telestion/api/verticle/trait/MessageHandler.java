package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.JsonRecord;

/**
 * An event handler which accepts the decoded body of the message
 * and returns nothing.
 *
 * @author Ludwig Richter (@fussel178)
 * @see WithEventBus#register(String, MessageHandler, Class)
 */
@FunctionalInterface
public interface MessageHandler<T extends JsonRecord> {
	void handle(T body);
}
