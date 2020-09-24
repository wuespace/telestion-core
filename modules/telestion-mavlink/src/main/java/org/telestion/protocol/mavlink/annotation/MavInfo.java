package org.telestion.protocol.mavlink.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.telestion.protocol.mavlink.message.MavlinkMessage;

/**
 * An {@link Annotation} providing more information about a MAVLink-Message.
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavInfo {
	/**
	 * Equivalent to the message id of the MAVLink-Specifications.
	 * 
	 * @return message id
	 */
	public int id();
	/**
	 * The calculated CRC_EXTRA byte for this message.
	 * 
	 * @return CRC_EXTRA byte for this message
	 */
	public int crc();
}
