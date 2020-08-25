package org.telestion.adapter.mavlink.message;

import java.lang.reflect.InvocationTargetException;

import org.telestion.api.message.JsonMessage;

/**
 * Template for {@link JsonMessage JsonMessages} which are used for the MAVLINK-Protocol.
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see JsonMessage
 */
public interface MavlinkMessage extends JsonMessage {
	
	/**
	 * 
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	default BaseMessage toRaw() {
		return new BaseMessage();
	}
}
