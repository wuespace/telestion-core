package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record RawMessage(@JsonProperty byte[] data) implements JsonMessage {
	private RawMessage() {
		this(null);
	}
}
