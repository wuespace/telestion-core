package org.telestion.core.connection.old;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

/**
 *
 */
public record ConnectionData(@JsonProperty ConnectionDetails details,
							 @JsonProperty byte[] data) implements JsonMessage {

	@SuppressWarnings("unused")
	private ConnectionData() {
		this(null, null);
	}
}
