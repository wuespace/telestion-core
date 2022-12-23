package de.wuespace.telestion.services.connection.rework;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonRecord;

public record RawMessage(@JsonProperty byte[] data) implements JsonRecord {
	@SuppressWarnings("unused")
	private RawMessage() {
		this(null);
	}
}
