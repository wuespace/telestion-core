package de.wuespace.telestion.examples.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonRecord;

public record SimpleMessage(
		@JsonProperty String title,
		@JsonProperty String content
) implements JsonRecord {
}
