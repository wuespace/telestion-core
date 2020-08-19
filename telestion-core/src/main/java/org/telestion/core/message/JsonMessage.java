package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.spi.json.JsonCodec;

/**
 * The base class for all messages which are automatically encoded with the {@link JsonMessageCodec}.
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
    default String json() {
       return JsonCodec.INSTANCE.toString(this);
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
}
