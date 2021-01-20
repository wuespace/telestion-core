package de.jvpichowski.rocketsound.messages.sound;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

// TODO: float not yet supported by vertx -> changed floats to double (Jan)
public record Spectrum(@JsonProperty double min, @JsonProperty double max, @JsonProperty double[] data) implements JsonMessage {
	@SuppressWarnings("unused")
	public Spectrum(){
		this(0.0, 0.0, null);
	}
}
