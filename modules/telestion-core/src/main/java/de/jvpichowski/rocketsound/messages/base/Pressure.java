package de.jvpichowski.rocketsound.messages.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

// TODO: float not yet supported by vertx -> changed floats to double (Jan)
public record Pressure(@JsonProperty double pressure) implements JsonMessage {

	@SuppressWarnings("unused")
	public Pressure(){
		this(0.0);
	}
}
