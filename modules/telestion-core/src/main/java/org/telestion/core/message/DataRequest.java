package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.telestion.api.message.JsonMessage;

import java.util.List;
import java.util.Optional;

public record DataRequest(
		@JsonProperty List<String> classNames,
		@JsonProperty String operation,
		@JsonProperty JsonObject query) implements JsonMessage {
			private DataRequest() {
				this(List.of(""), "", new JsonObject());
			}
}
