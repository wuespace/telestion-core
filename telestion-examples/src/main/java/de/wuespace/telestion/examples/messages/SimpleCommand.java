package de.wuespace.telestion.examples.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonRecord;

public record SimpleCommand(
		@JsonProperty String command,
		@JsonProperty String[] args
) implements JsonRecord {
}
