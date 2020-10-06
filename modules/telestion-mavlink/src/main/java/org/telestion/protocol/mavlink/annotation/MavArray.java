package org.telestion.protocol.mavlink.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.telestion.protocol.mavlink.message.MavlinkMessage;

/**
 * An {@link Annotation} specifying that a MAVLink-Message-Field is an array.<br>
 * Can be used to obtain more information about this array, too.
 *
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavArray {
	/**
	 * Returns the length of the array.
	 *
	 * @return length of the array
	 */
	public int length();
}
