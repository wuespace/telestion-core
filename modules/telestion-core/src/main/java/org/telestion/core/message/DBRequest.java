package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.telestion.api.message.JsonMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * dataType - to determine where to search
 * query (Optional) - to search for specific data values
 */
public record DBRequest(
        @JsonProperty Class<?> dataType,
        @JsonProperty Optional<JsonObject> query) implements JsonMessage {

    private DBRequest() { this(null, Optional.of(new JsonObject())); }
}
