package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.JsonMessage;

/**
 * {@link FunctionalInterface} which is used to handle Vert.X messages from the event bus. By inferring the generics of
 * this class with a {@link JsonMessage}, an implementation can link a raw message to a {@link JsonMessage} again,
 * before calling the handler itself - {@link #handle(JsonMessage)}.
 *
 * @param <T>	{@link JsonMessage} to handle
 * @author Cedric Boes (cb0s), Ludwig Richter (@fussel178), Pablo Klaschka (@pklaschka)
 */
@FunctionalInterface
public interface MessageHandler<T extends JsonMessage> {
	/**
	 * The function handling the parsed message object.
	 *
	 * @param body parsed {@link JsonMessage} to handle
	 */
	void handle(T body);
}
