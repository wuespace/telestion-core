package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The base class for all messages which are automatecally encoded with the {@link JsonMessageCodec}.
 * The subclasses have to be valid json classes.
 * This means that they could be encoded by {@link io.vertx.core.spi.json.JsonCodec} which is backed by
 * {@link io.vertx.core.json.jackson.JacksonCodec}.
 */
public interface JsonMessage {

    /**
     *
     * @return the simple class name of the subclass
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    default String name() {
        return getClass().getSimpleName();
    }
}
