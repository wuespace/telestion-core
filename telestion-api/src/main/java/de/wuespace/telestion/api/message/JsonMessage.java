package de.wuespace.telestion.api.message;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.json.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h2>Description</h2>
 * The base class for all messages which are automatically encoded with the JsonMessageCodec.
 * <p>
 * All subclasses have to be valid json classes. This means that they could be encoded by
 * {@link JsonCodec} which is backed by {@link io.vertx.core.json.jackson.JacksonCodec JacksonCodec}.
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * public record TimeMessage(
 *     @JsonProperty long receiveTime,
 *     @JsonProperty long sendTime
 * ) implements JsonMessage {
 * }
 * }
 * </pre>
 *
 * @author Jan von Pichowski (@jvpichowski), Cedric Boes (@cb0s), Ludwig Richter (@fussel178)
 */
public interface JsonMessage {

	///////////////////////////////////////////////////////////////////////////
	// asynchronous decoding section
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Asynchronous version of {@link #from(Buffer, Class)}.
	 *
	 * @param type             the class of the target {@link JsonMessage}
	 * @param json             the buffer whose contents contain the necessary information to construct
	 *                         the specified {@link JsonMessage}
	 * @param handler          gets called when the conversion was successful
	 * @param exceptionHandler gets called when a {@link DecodeException} occurred during conversion
	 * @param <T>              the type of the target {@link JsonMessage}
	 * @return {@code true} when the conversion was successful
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, Buffer json, Handler<T> handler,
											  Handler<RuntimeException> exceptionHandler) {
		try {
			handler.handle(from(json, type));
			return true;
		} catch (DecodeException | IllegalArgumentException e) {
			logger.warn("Cannot convert buffer to JsonMessage {}:", type.getName(), e);
			exceptionHandler.handle(e);
			return false;
		}
	}

	/**
	 * Asynchronous version of {@link #from(String, Class)}.
	 *
	 * @param type             the class of the target {@link JsonMessage}
	 * @param json             the JSON {@link String} that contains the necessary information to construct
	 *                         the specified {@link JsonMessage}
	 * @param handler          gets called when the conversion was successful
	 * @param exceptionHandler gets called when a {@link DecodeException} or an {@link IllegalArgumentException}
	 *                         occurred during conversion
	 * @param <T>              the type of the target {@link JsonMessage}
	 * @return {@code true} when the conversion was successful
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, String json, Handler<T> handler,
											  Handler<RuntimeException> exceptionHandler) {
		try {
			handler.handle(from(json, type));
			return true;
		} catch (DecodeException | IllegalArgumentException e) {
			logger.warn("Cannot convert JSON string to JsonMessage {}:", type.getName(), e);
			exceptionHandler.handle(e);
			return false;
		}
	}

	/**
	 * Asynchronous version of {@link #from(Object, Class)}.
	 *
	 * @param type             the class of the target {@link JsonMessage}
	 * @param json             the plain {@link Object} that contains the necessary information to construct
	 *                         the specified {@link JsonMessage}
	 * @param handler          gets called when the conversion was successful
	 * @param exceptionHandler gets called when a {@link DecodeException} or an {@link IllegalArgumentException}
	 *                         occurred during conversion
	 * @param <T>              the type of the target {@link JsonMessage}
	 * @return {@code true} when the conversion was successful
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, Object json, Handler<T> handler,
											  Handler<RuntimeException> exceptionHandler) {
		try {
			handler.handle(from(json, type));
			return true;
		} catch (DecodeException | IllegalArgumentException e) {
			logger.warn("Cannot convert Object to JsonMessage {}:", type.getName(), e);
			exceptionHandler.handle(e);
			return false;
		}
	}

	/**
	 * Asynchronous version of {@link #from(Message, Class)}.
	 *
	 * @param type             the class of the target {@link JsonMessage}
	 * @param message          the message whose body contains the necessary information to construct
	 *                         the specified {@link JsonMessage}
	 * @param handler          gets called when the conversion was successful
	 * @param exceptionHandler gets called when a {@link DecodeException} or an {@link IllegalArgumentException}
	 *                         occurred during conversion
	 * @param <T>              the type of the target {@link JsonMessage}
	 * @return {@code true} when the conversion was successful
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, Message<?> message, Handler<T> handler,
											  Handler<RuntimeException> exceptionHandler) {
		try {
			handler.handle(from(message, type));
			return true;
		} catch (DecodeException | IllegalArgumentException e) {
			logger.warn("Cannot convert Vertx Message to JsonMessage {}:", type.getName(), e);
			exceptionHandler.handle(e);
			return false;
		}
	}

	/**
	 * Like {@link #on(Class, Buffer, Handler, Handler)} but without an exception handler.
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, Buffer json, Handler<T> handler) {
		return on(type, json, handler, e -> {
		});
	}

	/**
	 * Like {@link #on(Class, String, Handler, Handler)} but without an exception handler.
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, String json, Handler<T> handler) {
		return on(type, json, handler, e -> {
		});
	}

	/**
	 * Like {@link #on(Class, Object, Handler, Handler)} but without an exception handler.
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, Object json, Handler<T> handler) {
		return on(type, json, handler, e -> {
		});
	}

	/**
	 * Like {@link #on(Class, Message, Handler, Handler)} but without an exception handler.
	 */
	static <T extends JsonMessage> boolean on(Class<T> type, Message<?> message, Handler<T> handler) {
		return on(type, message, handler, e -> {
		});
	}

	/**
	 * Like {@link #on(Class, Buffer, Handler, Handler)} but returns a {@link Future} which resolves
	 * when the conversion completes successfully or fails when a {@link DecodeException} or an
	 * {@link IllegalArgumentException} occurs during conversion.
	 *
	 * @return a future that represents the conversion state
	 */
	static <T extends JsonMessage> Future<T> on(Class<T> type, Buffer json) {
		return Future.future(promise -> on(type, json, promise::complete, promise::fail));
	}

	/**
	 * Like {@link #on(Class, String, Handler, Handler)} but returns a {@link Future} which resolves
	 * when the conversion completes successfully or fails when a {@link DecodeException} or an
	 * {@link IllegalArgumentException} occurs during conversion.
	 *
	 * @return a future that represents the conversion state
	 */
	static <T extends JsonMessage> Future<T> on(Class<T> type, String json) {
		return Future.future(promise -> on(type, json, promise::complete, promise::fail));
	}

	/**
	 * Like {@link #on(Class, Object, Handler, Handler)} but returns a {@link Future} which resolves
	 * when the conversion completes successfully or fails when a {@link DecodeException} or an
	 * {@link IllegalArgumentException} occurs during conversion.
	 *
	 * @return a future that represents the conversion state
	 */
	static <T extends JsonMessage> Future<T> on(Class<T> type, Object json) {
		return Future.future(promise -> on(type, json, promise::complete, promise::fail));
	}

	/**
	 * Like {@link #on(Class, Message, Handler, Handler)} but returns a {@link Future} which resolves
	 * when the conversion completes successfully or fails when a {@link DecodeException} occurs during conversion.
	 *
	 * @return a future that represents the conversion state
	 */
	static <T extends JsonMessage> Future<T> on(Class<T> type, Message<?> message) {
		return Future.future(promise -> on(type, message, promise::complete, promise::fail));
	}

	///////////////////////////////////////////////////////////////////////////
	// synchronous decoding section
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link JsonMessage} from a buffer which contains an encoded JSON string.
	 *
	 * @param json the buffer that contents contain the necessary information to construct
	 *             the specified {@link JsonMessage}
	 * @param type the class of the target {@link JsonMessage}
	 * @param <T>  the type of the target {@link JsonMessage}
	 * @return the decoded message
	 * @throws DecodeException if the buffer contents does not contain the necessary information to successfully
	 *                         construct the specified {@link JsonMessage}
	 */
	static <T extends JsonMessage> T from(Buffer json, Class<T> type) throws DecodeException {
		return Json.CODEC.fromBuffer(json, type);
	}

	/**
	 * Constructs a {@link JsonMessage} from a JSON {@link String}.
	 *
	 * @param json the JSON {@link String} that contains the necessary information to construct
	 *             the specified {@link JsonMessage}
	 * @param type the class of the target {@link JsonMessage}
	 * @param <T>  the type of the target {@link JsonMessage}
	 * @return the decoded message
	 * @throws DecodeException if the JSON string does not contain the necessary information to successfully
	 *                         construct the specified {@link JsonMessage}
	 */
	static <T extends JsonMessage> T from(String json, Class<T> type) throws DecodeException {
		return Json.CODEC.fromString(json, type);
	}

	/**
	 * Constructs a {@link JsonMessage} from a plain {@link Object}.
	 *
	 * @param json the plain {@link Object} that contains the necessary information to construct
	 *             the specified {@link JsonMessage}
	 * @param type the class of the target {@link JsonMessage}
	 * @param <T>  the type of the target {@link JsonMessage}
	 * @return the decoded message
	 * @throws DecodeException if the plain object does not contain the necessary information to successfully
	 *                         construct the specified {@link JsonMessage}
	 */
	static <T extends JsonMessage> T from(Object json, Class<T> type) throws DecodeException {
		return Json.CODEC.fromValue(json, type);
	}

	/**
	 * Constructs a {@link JsonMessage} from a Vert.x EventBus {@link Message} body.
	 *
	 * @param message the message which body contains the necessary information to construct
	 *                the specified {@link JsonMessage}
	 * @param type    the class of the target {@link JsonMessage}
	 * @param <T>     the type of the target {@link JsonMessage}
	 * @return the decoded message
	 * @throws DecodeException if the raw message body does not contain the necessary information to successfully
	 *                         construct the specified {@link JsonMessage}
	 */
	static <T extends JsonMessage> T from(Message<?> message, Class<T> type) throws DecodeException {
		return from(message.body(), type);
	}

	///////////////////////////////////////////////////////////////////////////
	// synchronous encoding section
	///////////////////////////////////////////////////////////////////////////

	/**
	 * @deprecated Use {@link #toJsonObject()} to get a JSON object representation of this message
	 * or use {@link #toJsonString()} to get a JSON string.
	 */
	@Deprecated(since = "0.9.0")
	default JsonObject json() throws IllegalArgumentException {
		return toJsonObject();
	}

	/**
	 * Constructs a {@link JsonObject} from the {@link JsonMessage}.
	 *
	 * @return the constructed JSON object
	 * @throws IllegalArgumentException if the JSON object cannot represent the type of
	 *                                  any {@link JsonMessage} property
	 */
	default JsonObject toJsonObject() throws IllegalArgumentException {
		return JsonObject.mapFrom(this);
	}

	/**
	 * Constructs a {@link String} containing the properties of the {@link JsonMessage} as JSON values.
	 *
	 * @param pretty if {@code true} the JSON output is properly formatted
	 * @return a JSON string representing the {@link JsonMessage}
	 * @throws EncodeException if the {@link JsonMessage} containing properties that cannot be represented
	 *                         by JSON values
	 * @see io.vertx.core.json.jackson.JacksonCodec#toString(Object, boolean)
	 */
	default String toJsonString(boolean pretty) throws EncodeException {
		return Json.CODEC.toString(this, pretty);
	}

	/**
	 * Like {@link #toJsonString(boolean)} but with space efficient JSON output.
	 */
	default String toJsonString() throws EncodeException {
		return toJsonString(false);
	}

	/**
	 * Constructs a {@link Buffer} containing the properties of the {@link JsonMessage} as JSON values
	 *
	 * @param pretty if {@code true} the JSON output is properly formatted
	 * @return a buffer representing the {@link JsonMessage}
	 * @throws EncodeException if the {@link JsonMessage} containing properties that cannot be represented
	 *                         by JSON values
	 * @see io.vertx.core.json.jackson.JacksonCodec#toBuffer(Object, boolean)
	 */
	default Buffer toJsonBuffer(boolean pretty) throws EncodeException {
		return Json.CODEC.toBuffer(this, pretty);
	}

	/**
	 * Like {@link #toJsonBuffer(boolean)} but with space efficient JSON output.
	 */
	default Buffer toJsonBuffer() throws EncodeException {
		return toJsonBuffer(false);
	}

	///////////////////////////////////////////////////////////////////////////
	// others section
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the simple class name of the implementing subclass.
	 *
	 * @return simple class name of subclass
	 */
	default String className() {
		return getClass().getName();
	}

	Logger logger = LoggerFactory.getLogger(JsonMessage.class);
}
