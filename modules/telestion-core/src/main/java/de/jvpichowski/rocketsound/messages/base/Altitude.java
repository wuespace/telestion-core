package de.jvpichowski.rocketsound.messages.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

// TODO: float not yet supported by vertx -> changed floats to double (Jan)
public record Altitude(@JsonProperty double temp) implements JsonMessage {

	@SuppressWarnings("unused")
	public Altitude(){
		this(0.0);
	}
}
