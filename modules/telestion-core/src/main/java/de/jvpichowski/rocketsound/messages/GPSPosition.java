package de.jvpichowski.rocketsound.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record GPSPosition(@JsonProperty float x, @JsonProperty float y, @JsonProperty float z) implements JsonMessage {

	@SuppressWarnings("unused")
	public GPSPosition(){
		this(0.0f, 0.0f, 0.0f);
	}
}
