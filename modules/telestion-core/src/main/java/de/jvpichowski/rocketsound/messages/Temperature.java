package de.jvpichowski.rocketsound.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record Temperature(@JsonProperty float temp) implements JsonMessage {

	@SuppressWarnings("unused")
	public Temperature(){
		this(0.0f);
	}
}
