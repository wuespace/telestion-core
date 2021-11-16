package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record IpDetails(@JsonProperty
						String ip,
						@JsonProperty
						int port) implements JsonMessage {
	public IpDetails() {
		this("0.0.0.0", 0);
	}
}
