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
	public static final String SERIAL_DEVICE = "serial-device";
	public static final String SERIAL_BAUDRATE = "serial-baudrate";
	// ip stuff
	public static final String HOST_IP = "host-ip";
	public static final String HOST_NAME = "host-name";
	public static final String HOST_PORT = "host-port";
	// message stuff
	public static final String MESSAGE_ID = "message-id";
	public static final String MESSAGE_NAME = "message-name";
	public static final String MESSAGE_TYPE = "message-type";
	// time stuff
	public static final String TIME_CREATED = "time-created";
	public static final String TIME_MODIFIED = "time-modified";
	public static final String TIME_ACCESSED = "time-accessed";
	public static final String TIME_RECEIVED = "time-received";
	public static final String TIME_CURRENT = "time-current";
}
