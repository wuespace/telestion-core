package de.jvpichowski.rocketsound.messages.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record BaroData(@JsonProperty Pressure press, @JsonProperty Temperature temp, @JsonProperty Altitude alt) implements JsonMessage {

	@SuppressWarnings("unused")
	public BaroData(){
		this(null, null, null);
	}
}
