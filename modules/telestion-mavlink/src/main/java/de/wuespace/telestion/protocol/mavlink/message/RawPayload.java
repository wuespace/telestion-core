package de.wuespace.telestion.protocol.mavlink.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.protocol.mavlink.message.internal.RawMavlink;

/**
 * Wrapper for the payload of MAVLink-messages to easily use it in the {@link RawMavlink RawMavlinkV-messages}.
 *
 * @param payload Actual payload bytes of a MAVLink-message.
 * @author Cedric Boes
 * @version 1.0
 */
public final record RawPayload(@JsonProperty byte[] payload) implements JsonMessage {

	/**
	 * There shall be no default constructor for normal people.<br>
	 * This will only be used by the JSON-parser.
	 */
	@SuppressWarnings("unused")
	private RawPayload() {
		this(null);
	}
}
