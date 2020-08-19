package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Position(
        @JsonProperty double x,
        @JsonProperty double y,
        @JsonProperty double z) implements JsonMessage {

    private Position(){
        this(0.0, 0.0, 0.0);
    }
}
