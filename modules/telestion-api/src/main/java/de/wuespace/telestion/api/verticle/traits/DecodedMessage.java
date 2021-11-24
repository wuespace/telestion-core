package de.wuespace.telestion.api.verticle.traits;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;

public record DecodedMessage<T extends JsonMessage>(
		@JsonProperty T body,
		@JsonProperty Message<Object> message
		) implements JsonMessage {
}
