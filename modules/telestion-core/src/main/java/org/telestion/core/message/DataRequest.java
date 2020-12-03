package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.telestion.api.message.JsonMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record DataRequest(
		@JsonProperty List<Class<?>> dataTypes,
		@JsonProperty Optional<DataOperation> operation) implements JsonMessage {

	private DataRequest() {
		this(Collections.emptyList(),
			Optional.of(new DataOperation("", new JsonObject(), Optional.empty())));
	}
}
