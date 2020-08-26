package org.telestion.adapter.mavlink.message;

import org.telestion.adapter.mavlink.annotation.MavInfo;

/**
 * 
 * @author Cedric
 *
 */
@MavInfo(id = 0, crc = 0x50)
@SuppressWarnings("preview")
public record Hearbeat(
		int mavType,
		int autopilot,
		int baseMode,
		long customMode,
		int systemStatus,
		int mavVersion) implements MavlinkMessage {

}
