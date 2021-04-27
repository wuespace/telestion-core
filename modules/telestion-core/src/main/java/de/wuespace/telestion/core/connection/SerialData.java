package de.wuespace.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record SerialData(@JsonProperty byte[] data) implements JsonMessage {

	@SuppressWarnings("unused")
	private SerialData(){
		this(null);
	}
}
