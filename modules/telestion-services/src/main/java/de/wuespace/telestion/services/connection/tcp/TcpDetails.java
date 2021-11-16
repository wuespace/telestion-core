package de.wuespace.telestion.services.connection.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.services.connection.ConnectionDetails;
import de.wuespace.telestion.services.connection.IpDetails;

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

	public static TcpDetails fromIpDetails(IpDetails details) {
		return new TcpDetails(details.ip(), details.port());
	}
}
