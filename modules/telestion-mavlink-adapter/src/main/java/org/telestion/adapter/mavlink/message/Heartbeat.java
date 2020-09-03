package org.telestion.adapter.mavlink.message;

import org.telestion.adapter.mavlink.annotation.MavField;
import org.telestion.adapter.mavlink.annotation.MavInfo;
import org.telestion.adapter.mavlink.annotation.NativeType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 */
@MavInfo(id = 0, crc = 0x32)
@SuppressWarnings("preview")
public record Heartbeat(
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int mavType,
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int autopilot,
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int baseMode,
		@MavField(nativeType = NativeType.UINT_32)
		@JsonProperty long customMode,
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int systemStatus,
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int mavVersion) implements MavlinkMessage {

	static {
		// Register
		MessageIndex.put(0, Heartbeat.class);
	}
	
}
