package de.jvpichowski.rocketsound.messages.sound;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record Amplitude(@JsonProperty float amplitude) implements JsonMessage {
	@SuppressWarnings("unused")
	public Amplitude(){
		this(0.0f);
	}
}
