package de.wuespace.telestion.api.verticle.trait;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.eventbus.Message;

/**
 * A Record extending {@link JsonMessage} containing, both, the parsed JsonMessage and the raw message.
 *
 * @param body parsed message which is easier to use than the raw JSON
 * @param message raw, unparsed message
 * @param <T>	type of parsed {@link JsonMessage}
 * @author Cedric Boes (cb0s), Ludwig Richter (@fussel178), Pablo Klaschka (@pklaschka)
 */
public record DecodedMessage<T extends JsonMessage>(
		@JsonProperty T body,
		@JsonProperty Message<Object> message
		) implements JsonMessage {
}
