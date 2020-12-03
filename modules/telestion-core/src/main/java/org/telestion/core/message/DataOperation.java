package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.telestion.api.message.JsonMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record DataOperation(
		@JsonProperty String operationAddress,
		@JsonProperty JsonObject data,
		@JsonProperty Optional<JsonObject> params) implements JsonMessage {

	private DataOperation() { this("", new JsonObject(), Optional.of(new JsonObject())); }
}
