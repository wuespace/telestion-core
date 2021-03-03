package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

public record SerialData(@JsonProperty byte[] data) implements JsonMessage {

	@SuppressWarnings("unused")
	private SerialData(){
		this(null);
	}
}
