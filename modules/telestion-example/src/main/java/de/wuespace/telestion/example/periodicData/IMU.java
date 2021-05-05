package de.wuespace.telestion.example.periodicData;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record IMU(
		@JsonProperty double imuAccX,
		@JsonProperty double imuAccY,
		@JsonProperty double imuAccZ,
		@JsonProperty double imuGyroX,
		@JsonProperty double imuGyroY,
		@JsonProperty double imuGyroZ
) implements JsonMessage {
	private IMU() {
		this(.0, .0, .0, .0, .0, .0);
	}
}
