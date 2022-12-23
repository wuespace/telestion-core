package de.wuespace.telestion.api.verticle.trait;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonRecord;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * A wrapper for a {@link Message Vert.x response message} and its body as {@link JsonRecord}.
 * Used to wrap the response message and its body in a single object.
 *
 * @param message the raw {@link Message Vert.x message}
 * @param body    the decoded body of the {@link Message Vert.x message}
 * @param <V>     the type of {@link JsonRecord} to map to
 * @param <T>     the type of the body of the {@link Message Vert.x message}
 * @see JsonRecord#on(Class, Message)
 *
 * @author Ludwig Richter (@fussel178)
 */
public record ResponseMessageWrapper<V extends JsonRecord, T extends JsonObject>(
		@JsonProperty V body,
		@JsonProperty Message<T> message
) implements JsonRecord {

	/**
	 * Returns a {@link Future} which succeeds with the {@code messageFuture}'s body as {@link JsonRecord}.
	 * Fails if the {@code messageFuture}'s body cannot be mapped to the {@link JsonRecord} type
	 * or the {@code messageFuture} fails.
	 *
	 * @param clazz         the class type of the {@link JsonRecord} to map to
	 * @param messageFuture the future that returns the received message
	 * @param <V>           the type of {@link JsonRecord} to map to
	 * @param <T>           the type of the body of the {@link Message Vert.x message}
	 * @return a new {@link Future} which succeeds with the {@code messageFuture}'s body as {@link JsonRecord}
	 * @see JsonRecord#on(Class, Message)
	 */
	public static <V extends JsonRecord, T extends JsonObject> Future<ResponseMessageWrapper<V, T>> compose(
			Class<V> clazz,
			Future<Message<T>> messageFuture) {
		return messageFuture.compose(message -> on(clazz, message));
	}

	/**
	 * Returns a {@link Future} which succeeds with the {@link Message Vert.x message}'s body as {@link JsonRecord}.
	 * Fails if the {@link Message Vert.x message}'s body cannot be mapped to the {@link JsonRecord} type.
	 *
	 * @param clazz   the class type of the {@link JsonRecord} to map to
	 * @param message the {@link Message Vert.x message}
	 * @param <V>     the type of {@link JsonRecord} to map to
	 * @param <T>     the type of the body of the {@link Message Vert.x message}
	 * @return a new {@link Future} which returns a {@link ResponseMessageWrapper} with the message's contents
	 * @see JsonRecord#on(Class, Message)
	 */
	public static <V extends JsonRecord, T extends JsonObject> Future<ResponseMessageWrapper<V, T>> on(
			Class<V> clazz,
			Message<T> message) {
		return JsonRecord.on(clazz, message).map(decoded -> new ResponseMessageWrapper<>(decoded, message));
	}
}
