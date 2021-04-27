package de.wuespace.telestion.services.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.List;
import de.wuespace.telestion.api.message.JsonMessage;

public record DbResponse(
		@JsonProperty Class<?> dataType,
		@JsonProperty List<JsonObject> result) implements JsonMessage {
			private DbResponse() {
				this(null, Collections.emptyList());
			}
}
