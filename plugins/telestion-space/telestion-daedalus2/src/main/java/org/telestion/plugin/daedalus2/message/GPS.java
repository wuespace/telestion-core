package org.telestion.plugin.daedalus2.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

/**
 * A simple GPS data typ which is an example for project specific messages.
 * TODO remove this class in production
 */
public record GPS(
        @JsonProperty long timestamp,
        @JsonProperty double x,
        @JsonProperty double y,
        @JsonProperty double z) implements JsonMessage {

    private GPS(){
        this(0, 0.0, 0.0, 0.0);
    }
}
