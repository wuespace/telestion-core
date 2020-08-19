package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

import java.util.List;

/**
 * A list message containing multiple positions.
 */
public record Positions(@JsonProperty List<Position> list) implements JsonMessage {
    private Positions(){
        this(null);
    }
}
