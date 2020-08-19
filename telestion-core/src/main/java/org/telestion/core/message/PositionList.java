package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

import java.util.List;

public record PositionList(@JsonProperty List<Position> positions) implements JsonMessage {
    private PositionList(){
        this(null);
    }
}
