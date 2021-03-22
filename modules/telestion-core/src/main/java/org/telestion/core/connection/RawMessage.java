package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record RawMessage(@JsonProperty byte[] data) implements JsonMessage {
	@SuppressWarnings("unused")
	private RawMessage() {
		this(null);
	}
}
