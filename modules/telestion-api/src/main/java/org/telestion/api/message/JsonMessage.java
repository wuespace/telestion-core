package org.telestion.api.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for all messages which are automatically encoded with the JsonMessageCodec.<br>
 * All subclasses have to be valid json classes. This means that they could be encoded by
 * {@link io.vertx.core.spi.json.JsonCodec} which is backed by {@link io.vertx.core.json.jackson.JacksonCodec}.
 *
 * @author Jan von Pichowski, Cedric Boes
 * @version 1.2
 */
@SuppressWarnings("preview")
public interface JsonMessage {
	JsonCodec JSON_CODEC = new JacksonCodec();
	Logger logger = LoggerFactory.getLogger(JsonMessage.class);
	/**
	 * This method decodes a {@link JsonMessage} from the event bus.<br>
	 * Returns whether decoding was successful or not.
	 *
	 * @param clazz   Class of the message-object
	 * @param msgBody {@link Message#body() msg-body} of the sent message
	 * @param handler handler for the message
	 * @param <T>     Generic type for the {@link Handler}
	 * @return whether decoding was successful
	 */
	static <T extends JsonMessage> boolean on(Class<T> clazz, Object msgBody, Handler<T> handler) {
		if (msgBody instanceof JsonObject jsonObject && jsonObject.containsKey("className")) {
			return reconstructMsg(clazz, jsonObject, handler);
		} else {
			return false;
		}
	}

	/**
	 * This method decodes a {@link JsonMessage} from the event bus.<br>
	 * Returns whether decoding was successful or not.
	 *
	 * @param clazz   Class of the message-object
	 * @param msg     sent message
	 * @param handler handler for the message
	 * @param <T>     type of the {@link Message}
	 * @return whether decoding was successful
	 */
	static <T extends JsonMessage> boolean on(Class<T> clazz, Message<?> msg, Handler<T> handler) {
		if (msg.body() instanceof JsonObject jsonObject && jsonObject.containsKey("className")) {
			return reconstructMsg(clazz, jsonObject, handler);
		} else {
			return false;
		}
	}

	/**
	 * Creates a message from the given json representation.
	 *
	 * @param json source
	 * @param type class of the message
	 * @param <T>  type of the {@link Message}
	 * @return decoded message object
	 */
	static <T extends JsonMessage> T from(String json, Class<T> type) {
		return JSON_CODEC.fromString(json, type);
	}

	/**
	 * Creates a message from the given json representation.
	 *
	 * @param json source which must be a String
	 * @param type class of the message
	 * @param <T>  type of the {@link Message}
	 * @return decoded message object
	 */
	static <T extends JsonMessage> T from(Object json, Class<T> type) {
		return JSON_CODEC.fromString((String) json, type);
	}

	/**
	 * Returns the simple class name of the subclass.
	 *
	 * @return simple class name of subclass
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	default String className() {
		return getClass().getName();
	}

	/**
	 * Returns the Json-representation of the message.
	 *
	 * @return json representation of message
	 */
	default JsonObject json() {
		return JsonObject.mapFrom(this);
	}

	/**
	 * Reflection part of {@link #on(Class, Message, Handler)} and {@link #on(Class, Object, Handler)} which constructs
	 * the original message.
	 *
	 * @param clazz			Class to cast to
	 * @param jsonObject	Object to be casted
	 * @param handler		Handler for the message
	 * @param <T>			Type of the message
	 * @return if parsing successful
	 */
	private static <T extends JsonMessage> boolean reconstructMsg(Class<T> clazz, JsonObject jsonObject,
																  Handler<T> handler) {
		try {
			var msgClazz = Class.forName(jsonObject.getString("className"));
			if (!clazz.isAssignableFrom(msgClazz)) {
				return false;
			}
			handler.handle((clazz.cast(jsonObject.mapTo(msgClazz))));
			return true;
		} catch (ClassNotFoundException e) {
			logger.error("Error while converting JSON into JsonMessage. The requested message is not available on the" +
					" classpath.", e);
			return false;
		}
	}
}
