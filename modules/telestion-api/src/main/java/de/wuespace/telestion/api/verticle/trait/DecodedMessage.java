package de.wuespace.telestion.api.verticle.trait;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * A wrapper for a decoded message from the {@link Message Vert.x message} to the {@link JsonMessage} type
 * and basis {@link Message Vert.x message}
 *
 * @param message the {@link Message Vert.x message}
 * @param body    the decoded body of the {@link Message Vert.x message}
 * @param <V>     the type of {@link JsonMessage} to map to
 * @param <T>     the type of the body of the {@link Message Vert.x message}
 * @see JsonMessage#on(Class, Message)
 *
 * @author Ludwig Richter (@fussel178)
 */
public record DecodedMessage<V extends JsonMessage, T extends JsonObject>(
		@JsonProperty V body,
		@JsonProperty Message<T> message
) implements JsonMessage {

	/**
	 * Composes the given future, which eventually returns the received message, with the returned future
	 * to map from the {@link Message Vert.x message} to the {@link JsonMessage} type.
	 *
	 * @param clazz         the class type of the {@link JsonMessage} to map to
	 * @param messageFuture the future that returns the received message
	 * @param <V>           the type of {@link JsonMessage} to map to
	 * @param <T>           the type of the body of the {@link Message Vert.x message}
	 * @return a new future composed from the passed future that maps to the {@link JsonMessage} type
	 * @see JsonMessage#on(Class, Message)
	 */
	public static <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> compose(
			Class<V> clazz,
			Future<Message<T>> messageFuture) {
		return messageFuture.compose(message -> on(clazz, message));
	}

	/**
	 * Decodes the {@link Message Vert.x message} to the {@link JsonMessage} type and returns a {@link Future}
	 * which eventually returns a {@link DecodedMessage} with the message's contents.
	 *
	 * @param clazz   the class type of the {@link JsonMessage} to map to
	 * @param message the {@link Message Vert.x message}
	 * @param <V>     the type of {@link JsonMessage} to map to
	 * @param <T>     the type of the body of the {@link Message Vert.x message}
	 * @return a new future which eventually returns a {@link DecodedMessage} with the message's contents
	 * @see JsonMessage#on(Class, Message)
	 */
	public static <V extends JsonMessage, T extends JsonObject> Future<DecodedMessage<V, T>> on(
			Class<V> clazz,
			Message<T> message) {
		return JsonMessage.on(clazz, message).map(decoded -> new DecodedMessage<>(decoded, message));
	}
}
