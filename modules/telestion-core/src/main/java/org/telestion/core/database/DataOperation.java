package org.telestion.core.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import java.util.Optional;
import org.telestion.api.message.JsonMessage;

public record DataOperation(
		@JsonProperty String operationAddress,
		@JsonProperty JsonObject data,
		@JsonProperty Optional<JsonObject> params) implements JsonMessage {
			private DataOperation() {
				this("", new JsonObject(), Optional.of(new JsonObject()));
			}
}
