package de.wuespace.telestion.core.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import de.wuespace.telestion.api.message.JsonMessage;

public record DataOperation(
		@JsonProperty JsonObject data,
		@JsonProperty JsonObject params) implements JsonMessage {
			private DataOperation() {
				this(new JsonObject(), new JsonObject());
			}
}
