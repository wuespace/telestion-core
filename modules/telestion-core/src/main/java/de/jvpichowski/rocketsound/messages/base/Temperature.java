package de.jvpichowski.rocketsound.messages.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

// TODO: float not yet supported by vertx -> changed floats to double (Jan)
public record Temperature(@JsonProperty double temp) implements JsonMessage {

	@SuppressWarnings("unused")
	public Temperature(){
		this(0.0);
	}
}
