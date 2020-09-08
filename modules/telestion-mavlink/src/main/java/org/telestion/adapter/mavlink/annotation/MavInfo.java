package org.telestion.adapter.mavlink.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.telestion.adapter.mavlink.message.MavlinkMessage;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavInfo {
	/**
	 * 
	 * @return
	 */
	public int id();
	/**
	 * 
	 * @return
	 */
	public int crc();
}
