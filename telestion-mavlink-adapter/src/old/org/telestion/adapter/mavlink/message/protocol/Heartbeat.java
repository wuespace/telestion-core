package org.telestion.adapter.mavlink.message.protocol;

import org.telestion.adapter.mavlink.annotation.MavComponentInfo;
import org.telestion.adapter.mavlink.annotation.MavComponentType;
import org.telestion.adapter.mavlink.message.MavlinkMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO: Description
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 *
 */
@SuppressWarnings("preview")
public record Heartbeat(
		
		/**
		 * TODO: Description
		 */
		@JsonProperty
		@MavComponentInfo(type = MavComponentType.UNSIGNED_INT_8, position = 0)
		int type,
		
		/**
		 * TODO: Description
		 */
		@JsonProperty
		@MavComponentInfo(type = MavComponentType.UNSIGNED_INT_8, position = 1)
		int autopilot,
		
		/**
		 * TODO: Description
		 */
		@JsonProperty
		@MavComponentInfo(type = MavComponentType.UNSIGNED_INT_8, position = 2)
		int baseMode,
		
		/**
		 * TODO: Description
		 */
		@JsonProperty
		@MavComponentInfo(type = MavComponentType.UNSIGNED_INT_32, position = 3)
		long customMode,
		
		/**
		 * TODO: Description
		 */
		@JsonProperty
		@MavComponentInfo(type = MavComponentType.UNSIGNED_INT_8, position = 4)
		int systemStatus,
		
		/**
		 * TODO: Description
		 */
		@JsonProperty
		@MavComponentInfo(type = MavComponentType.UNSIGNED_INT_8, position = 5)
		int mavlinkVersion
		
		) implements MavlinkMessage {
	
	@SuppressWarnings("unused")
	private Heartbeat() {
		this(-1, -1, -1, -1l, -1, -1);
	}

}
