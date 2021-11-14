package de.wuespace.telestion.services.connection.udp;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.services.connection.ConnectionDetails;

public record UdpDetails(@JsonProperty String ip,
						 @JsonProperty int port,
						 @JsonProperty int packetId) implements ConnectionDetails {
	private UdpDetails() {
		this(null, 0, 0);
	}
}
