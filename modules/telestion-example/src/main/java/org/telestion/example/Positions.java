package org.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;
import java.util.List;

/**
 * A list message containing multiple {@link Position}.
 */
@SuppressWarnings("preview")
public record Positions(@JsonProperty List<Position> list) implements JsonMessage {
    @SuppressWarnings("unused")
    private Positions() {
        this(null);
    }
}
