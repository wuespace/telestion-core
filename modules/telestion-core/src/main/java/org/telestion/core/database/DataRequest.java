package org.telestion.core.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.telestion.api.message.JsonMessage;

public record DataRequest(
		@JsonProperty String className,
		@JsonProperty JsonObject query,
		@JsonProperty String operation,
		@JsonProperty JsonObject operationParams) implements JsonMessage {
			private DataRequest() {
				this("", new JsonObject(), "", new JsonObject());
			}
}
