package org.telestion.core.connection.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.core.connection.ConnectionDetails;

public record TcpDetails(@JsonProperty String ip,
						 @JsonProperty int port,
						 @JsonProperty int packetId) implements ConnectionDetails {

	/**
	 * For reflection
	 */
	@SuppressWarnings("unused")
	private TcpDetails() {
		this(null, 0, 0);
	}

	public TcpDetails(String ip, int port) {
		this(ip, port, 0);
	}
}
