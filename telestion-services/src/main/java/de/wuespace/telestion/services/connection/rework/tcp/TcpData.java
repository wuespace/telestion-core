package de.wuespace.telestion.services.connection.rework.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonRecord;

public record TcpData(@JsonProperty byte[] data,
					  @JsonProperty TcpDetails details) implements JsonRecord {

	/**
	 * For reflection
	 */
	@SuppressWarnings("unused")
	private TcpData() {
		this(null, null);
	}
}
