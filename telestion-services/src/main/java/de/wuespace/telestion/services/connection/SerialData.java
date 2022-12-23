package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonRecord;

@Deprecated(since = "v0.1.3", forRemoval = true)
public record SerialData(@JsonProperty byte[] data) implements JsonRecord {

	@SuppressWarnings("unused")
	private SerialData(){
		this(null);
	}
}
