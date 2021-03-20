package org.telestion.core.connection.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record TcpData(@JsonProperty byte[] data,
					  @JsonProperty TcpDetails details) implements JsonMessage {

	/**
	 * For reflection
	 */
	@SuppressWarnings("unused")
	private TcpData() {
		this(null, null);
	}
}
