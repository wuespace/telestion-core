package de.jvpichowski.rocketsound.messages.sound;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record Spectrum(@JsonProperty float min, @JsonProperty float max, @JsonProperty float[] data) implements JsonMessage {
	@SuppressWarnings("unused")
	public Spectrum(){
		this(0.0f, 0.0f, null);
	}
}
