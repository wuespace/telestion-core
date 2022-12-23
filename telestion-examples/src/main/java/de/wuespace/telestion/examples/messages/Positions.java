package de.wuespace.telestion.examples.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import de.wuespace.telestion.api.message.JsonRecord;

/**
 * A list message containing multiple {@link Position}.
 */
public record Positions(@JsonProperty List<Position> list) implements JsonRecord {
}
