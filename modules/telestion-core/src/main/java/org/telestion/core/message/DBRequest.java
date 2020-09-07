package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import org.telestion.api.message.JsonMessage;

public record DBRequest(
        @JsonProperty String collection,
        @JsonProperty JsonObject query) implements JsonMessage {

    private DBRequest() { this("", new JsonObject()); }
}
