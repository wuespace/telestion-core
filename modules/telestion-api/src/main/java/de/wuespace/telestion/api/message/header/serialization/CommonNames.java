package de.wuespace.telestion.api.message.header.serialization;

import de.wuespace.telestion.api.utils.AbstractUtils;

/**
 * This is a(n) (incomplete) list of proposals for
 * {@link de.wuespace.telestion.api.message.header.Information information record components}.
 * <p>
 * It is recommended to use one of these field names for the serialization of the components of the header to increase
 * the developer experience, as future event bus apis for other programming languages will also use the same list.<br/>
 * This allows for out-of-the-box intercommunication between different languages.
 *
 * @author Ludwig Richter (@fussel178)
 */
public class CommonNames extends AbstractUtils {
	// serial stuff
	/**
	 * Recommended name to specify the serial device.
	 */
	public static final String SERIAL_DEVICE = "serial-device";
	/**
	 * Recommended name to specify the serial baudrate.
	 */
	public static final String SERIAL_BAUDRATE = "serial-baudrate";
	// ip stuff
	/**
	 * Recommended name to specify the ip address in the ip protocol.
	 */
	public static final String HOST_IP = "host-ip";
	/**
	 * Recommended name to specify the host name in the ip protocol.
	 */
	public static final String HOST_NAME = "host-name";
	/**
	 * Recommended name to specify the port of the host in the ip protocol.
	 */
	public static final String HOST_PORT = "host-port";
	// message stuff
	/**
	 * Recommended name for some type of message id in the header.
	 */
	public static final String MESSAGE_ID = "message-id";
	/**
	 * Recommended name for some type of message name in the header.
	 */
	public static final String MESSAGE_NAME = "message-name";
	/**
	 * Recommended name for the type of message.
	 */
	public static final String MESSAGE_TYPE = "message-type";
	// time stuff
	/**
	 * Recommended name for when the package got created.
	 */
	public static final String TIME_CREATED = "time-created";
	/**
	 * Recommended name for when a package was last modified.
	 */
	public static final String TIME_MODIFIED = "time-modified";
	/**
	 * Recommended name for when a package was last accessed.
	 */
	public static final String TIME_ACCESSED = "time-accessed";
	/**
	 * Recommended name for when a package was received.
	 */
	public static final String TIME_RECEIVED = "time-received";
	/**
	 * Recommended name for communicating the current time to another verticle.
	 */
	public static final String TIME_CURRENT = "time-current";

	// This list is incomplete and will be extended in the future
}
