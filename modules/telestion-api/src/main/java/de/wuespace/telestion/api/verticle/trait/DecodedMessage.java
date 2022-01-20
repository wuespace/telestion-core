package de.wuespace.telestion.api.verticle.trait;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public record DecodedMessage<V extends JsonMessage, T extends JsonObject>(
		@JsonProperty V body,
		@JsonProperty Message<T> message
) implements JsonMessage {
}
