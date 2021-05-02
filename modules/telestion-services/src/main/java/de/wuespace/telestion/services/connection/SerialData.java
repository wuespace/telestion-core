package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

@Deprecated(since = "v0.1.3", forRemoval = true)
public record SerialData(@JsonProperty byte[] data) implements JsonMessage {

	@SuppressWarnings("unused")
	private SerialData(){
		this(null);
	}
}
