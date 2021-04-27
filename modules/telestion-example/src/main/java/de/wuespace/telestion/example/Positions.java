package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import de.wuespace.telestion.api.message.JsonMessage;

/**
 * A list message containing multiple {@link Position}.
 */
public record Positions(@JsonProperty List<Position> list) implements JsonMessage {
	@SuppressWarnings("unused")
	private Positions() {
		this(null);
	}
}
