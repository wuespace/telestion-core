package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import java.util.Optional;
import org.telestion.api.message.JsonMessage;

/**
 * dataType - to determine where to search
 * query (Optional) - to search for specific data values
 */
public record DbRequest(
	@JsonProperty Class<?> dataType,
	@JsonProperty Optional<JsonObject> query) implements JsonMessage {
		private DbRequest() {
			this(null, Optional.of(new JsonObject()));
		}
}
