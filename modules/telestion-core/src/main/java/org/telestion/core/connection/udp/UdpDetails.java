package org.telestion.core.connection.udp;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.core.connection.ConnectionDetails;

public record UdpDetails(@JsonProperty String ip,
						 @JsonProperty int port,
						 @JsonProperty int packetId) implements ConnectionDetails {
	private UdpDetails() {
		this(null, 0, 0);
	}
}
