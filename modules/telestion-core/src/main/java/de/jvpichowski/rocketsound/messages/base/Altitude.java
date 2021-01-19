package de.jvpichowski.rocketsound.messages.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record Altitude (@JsonProperty float temp) implements JsonMessage {

	@SuppressWarnings("unused")
	public Altitude(){
		this(0.0f);
	}
}
