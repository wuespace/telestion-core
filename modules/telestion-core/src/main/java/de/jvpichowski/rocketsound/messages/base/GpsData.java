package de.jvpichowski.rocketsound.messages.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record GpsData(@JsonProperty int satCount, @JsonProperty int fix,
					  @JsonProperty float north, @JsonProperty float east,
					  @JsonProperty long time) implements JsonMessage {

	@SuppressWarnings("unused")
	public GpsData(){
		this(0, 0, 0.0f, 0.0f, 0);
	}
}
