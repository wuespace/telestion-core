package org.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

/**
 * Data class which contains a position. Its json looks like this:
 * <code>{"x":5.3,"y":4.2,"z":7.1,"name":"Position"}</code>
 */
@SuppressWarnings("preview")
public record Position(@JsonProperty double x, @JsonProperty double y, @JsonProperty double z) implements JsonMessage {

    @SuppressWarnings("unused")
    private Position() {
        this(0.0, 0.0, 0.0);
    }
}
