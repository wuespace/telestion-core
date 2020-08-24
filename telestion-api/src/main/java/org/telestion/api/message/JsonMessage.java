package org.telestion.api.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.json.JsonCodec;

/**
 * The base class for all messages which are automatically encoded with the JsonMessageCodec.
 * The subclasses have to be valid json classes.
 * This means that they could be encoded by {@link io.vertx.core.spi.json.JsonCodec} which is backed by
 * {@link io.vertx.core.json.jackson.JacksonCodec}.
 */
public interface JsonMessage {

    /**
     * @return the simple class name of the subclass
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    default String name() {
        return getClass().getSimpleName();
    }

    /**
     * @return the json representation of the message
     */
    default JsonObject json() {
        return JsonObject.mapFrom(this);
    }

    /**
     * This method decodes a JsonMessage from the event bus.
     *
     * @param clazz
     * @param msg
     * @param handler
     * @param <T>
     */
    static <T extends JsonMessage> void on(Class<T> clazz, Message<?> msg, Handler<T> handler){
        if(msg.body() instanceof JsonObject jsonObject) {
            if (!jsonObject.containsKey("name")) {
                return;
            }
            if (clazz.getSimpleName().equals(jsonObject.getString("name"))) {
                handler.handle(jsonObject.mapTo(clazz));
            }
        }
    }

    /**
     * Creates a message from the given json representation
     *
     * @param json the json source
     * @param type the type class of the message
     * @param <T> the type of the message
     * @return the message object
     */
    static <T extends JsonMessage> T from(String json, Class<T> type){
        return JsonCodec.INSTANCE.fromString(json, type);
    }

    /**
     * Creates a message from the given json representation
     *
     * @param json the json source which must be a String
     * @param type the type class of the message
     * @param <T> the type of the message
     * @return the message object
     */
    static <T extends JsonMessage> T from(Object json, Class<T> type){
        return JsonCodec.INSTANCE.fromString((String)json, type);
    }
}
