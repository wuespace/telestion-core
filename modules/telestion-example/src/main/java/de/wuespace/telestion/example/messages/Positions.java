package de.wuespace.telestion.example.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.example.messages.Position;

/**
 * A list message containing multiple {@link Position}.
 */
public record Positions(@JsonProperty List<Position> list) implements JsonMessage {
}
