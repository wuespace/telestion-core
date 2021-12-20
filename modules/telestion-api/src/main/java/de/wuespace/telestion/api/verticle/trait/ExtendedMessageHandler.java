package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;

/**
 * {@link FunctionalInterface} which is used to handle Vert.X messages from the event bus. By inferring the generics of
 * this class with a {@link JsonMessage}, an implementation can link a raw message to a {@link JsonMessage} again,
 * before calling the handler itself - {@link #handle(JsonMessage, Message)}.
 * <p>
 * The difference to {@link MessageHandler} is, that this handler still has access to the raw message, which allows it,
 * to e.g. access the message header and other delivery options.
 *
 * @param <T>	{@link JsonMessage} to handle
 * @author Cedric Boes (cb0s), Ludwig Richter (@fussel178), Pablo Klaschka (@pklaschka)
 */
@FunctionalInterface
public interface ExtendedMessageHandler<T extends JsonMessage> {
	/**
	 * The function handling the message.
	 *
	 * @param body parsed {@link JsonMessage} to handle
	 * @param message raw message before parsing
	 */
	void handle(T body, Message<Object> message);
}
