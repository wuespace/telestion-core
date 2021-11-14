package de.wuespace.telestion.services.connection.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.services.connection.ConnectionDetails;

/**
 *
 * @param serialPort
 *
 * @author Cedric Boes
 * @version 1.0
 */
public record SerialDetails(@JsonProperty String serialPort) implements ConnectionDetails {
	/**
	 * For json-messages.
	 */
	@SuppressWarnings("unused")
	private SerialDetails() {
		this(null);
	}
}
