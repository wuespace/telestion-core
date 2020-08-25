package org.telestion.adapter.mavlink.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.telestion.adapter.mavlink.message.MavlinkMessage;

/**
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 *
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavComponentInfo {
	/**
	 * 
	 * @return
	 */
	public MavComponentType type() default MavComponentType.INT_8;
	
	/**
	 * 
	 * @return
	 */
	public int position() default -1;
}
