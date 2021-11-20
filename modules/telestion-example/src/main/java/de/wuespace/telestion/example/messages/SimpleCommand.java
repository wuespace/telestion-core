package de.wuespace.telestion.example.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record SimpleCommand(
		@JsonProperty String command,
		@JsonProperty String[] args
) implements JsonMessage {
}
