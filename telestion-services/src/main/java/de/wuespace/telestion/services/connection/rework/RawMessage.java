package de.wuespace.telestion.services.connection.rework;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record RawMessage(@JsonProperty byte[] data) implements JsonMessage {
	@SuppressWarnings("unused")
	private RawMessage() {
		this(null);
	}
}
