package de.wuespace.telestion.example.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

/**
 * Data class which contains a position. Its json looks like this:
 * <code>{"x":5.3,"y":4.2,"z":7.1,"name":"Position"}</code>
 */
public record Position(
		@JsonProperty double x,
		@JsonProperty double y,
		@JsonProperty double z
) implements JsonMessage {
}
