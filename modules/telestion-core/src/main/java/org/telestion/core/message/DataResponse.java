package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.telestion.api.message.JsonMessage;

public record DataResponse(
	@JsonProperty JsonObject data) implements JsonMessage {
		private DataResponse() {
			this(new JsonObject());
		}
}
