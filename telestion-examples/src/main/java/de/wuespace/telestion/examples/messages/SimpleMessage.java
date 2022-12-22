package de.wuespace.telestion.examples.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record SimpleMessage(
		@JsonProperty String title,
		@JsonProperty String content
) implements JsonMessage {
}
