package de.jvpichowski.rocketsound.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NineDOF(@JsonProperty Accelerometer acc, @JsonProperty Gyroscope gyro, @JsonProperty Magnetometer mag) {

	@SuppressWarnings("unused")
	public NineDOF(){
		this(null, null, null);
	}
}
