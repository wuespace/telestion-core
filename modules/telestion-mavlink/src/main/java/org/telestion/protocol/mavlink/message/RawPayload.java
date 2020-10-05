package org.telestion.protocol.mavlink.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

/**
 * Wrapper for the payload of MAVLink-messages to easily use it in the {@link RawMavlink RawMavlinkV-messages}.
 *
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public final record RawPayload(
        /**
         * Actual payload bytes of a MAVLink-message.
         */
        @JsonProperty byte[] payload) implements JsonMessage {

    /**
     * There shall be no default constructor for normal people.</br>
     * This will only be used by the JSON-parser.
     */
    @SuppressWarnings("unused")
    private RawPayload() {
        this(null);
    }
}
