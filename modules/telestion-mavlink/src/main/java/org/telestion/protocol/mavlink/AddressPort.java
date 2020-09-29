package org.telestion.protocol.mavlink;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Linking an address and a port of a net-address.
 * 
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public final record AddressPort(
		/**
		 * Actual address of a net-address.
		 */
        @JsonProperty String address,
        /**
         * Port of a net-address.
         */
        @JsonProperty int port) {

	/**
	 * There shall be no default constructor for normal people.</br>
	 * This will only be used by the JSON-parser.
	 */
    @SuppressWarnings("unused")
    private AddressPort() {
        this(null, 0);
    }
}
