package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.telestion.api.message.JsonMessage;

public record DataOperation(
		@JsonProperty JsonObject data,
		@JsonProperty JsonObject params) implements JsonMessage {
			private DataOperation() {
				this(new JsonObject(), new JsonObject());
			}
}
