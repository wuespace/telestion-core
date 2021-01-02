package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import java.util.Optional;
import org.telestion.api.message.JsonMessage;

public record DbRequest(
		@JsonProperty Class<?> dataType,
		@JsonProperty Optional<JsonObject> query) implements JsonMessage {
			private DbRequest() {
				this(null, Optional.of(new JsonObject()));
			}
}
