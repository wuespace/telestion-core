package de.jvpichowski.rocketsound.messages.sound;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

// TODO: float not yet supported by vertx -> changed floats to double (Jan)
public record Amplitude(@JsonProperty double amplitude) implements JsonMessage {
	@SuppressWarnings("unused")
	public Amplitude(){
		this(0.0);
	}
}
