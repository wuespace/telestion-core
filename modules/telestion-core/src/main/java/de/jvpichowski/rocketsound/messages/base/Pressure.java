package de.jvpichowski.rocketsound.messages.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record Pressure(@JsonProperty float pressure) implements JsonMessage {

	@SuppressWarnings("unused")
	public Pressure(){
		this(0.0f);
	}
}
