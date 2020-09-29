package org.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

import java.util.List;

/**
 * A list message containing multiple {@link Position}.
 */
public record Positions(@JsonProperty List<Position> list) implements JsonMessage {
<<<<<<< Updated upstream
    private Positions(){
=======
    @SuppressWarnings("unused")
	private Positions() {
>>>>>>> Stashed changes
        this(null);
    }
}
