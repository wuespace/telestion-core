package org.telestion.adapter.mavlink.message;

import org.telestion.adapter.mavlink.annotation.MavComponentInfo;
import org.telestion.adapter.mavlink.annotation.MavComponentType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Cedric
 *
 */
@SuppressWarnings("preview")
public record BaseMessage(
		/**
		 * TODO: Description
		 */
		@JsonProperty
		@MavComponentInfo(type = MavComponentType.RAW, position = 0)
		byte[] bytes) implements MavlinkMessage {
	
	/**
	 * 
	 */
	BaseMessage() {
		this(new byte[0]);
	}
}
