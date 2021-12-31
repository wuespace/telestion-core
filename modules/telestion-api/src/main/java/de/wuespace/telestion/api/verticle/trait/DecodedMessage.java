package de.wuespace.telestion.api.verticle.trait;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;

public record DecodedMessage<V extends JsonMessage, T>(
		@JsonProperty V body,
		@JsonProperty Message<T> message
		) implements JsonMessage {
}
