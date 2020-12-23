package de.jvpichowski.rocketsound.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record Velocity(@JsonProperty double x, @JsonProperty double y, @JsonProperty double z) implements JsonMessage {

	@SuppressWarnings("unused")
	private Velocity() {
		this(0.0, 0.0, 0.0);
	}
}
