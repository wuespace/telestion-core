package org.telestion.adapter.mavlink.message.internal;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public record RawMavlinkV1(
		@JsonProperty short len,
		@JsonProperty short seq,
		@JsonProperty short sysId,
		@JsonProperty short compId,
		@JsonProperty short msgId,
		@JsonProperty RawPayload payload,
		@JsonProperty int checksum)implements RawMavlink {

	@SuppressWarnings("unused")
	private RawMavlinkV1() {
		this((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, null, 0);
	}
	
	public RawMavlinkV1(byte[] bytes) {
		this(	(short) bytes[1],
				(short) bytes[2],
				(short) bytes[3],
				(short) bytes[4],
				(short) bytes[5],
				new RawPayload(Arrays.copyOfRange(bytes, 6, bytes[1] + 6)),
				(int) (bytes[bytes[1] + 6] << 8) + bytes[bytes[1] + 7]);
	}
	
	@Override
	@JsonProperty(access = Access.READ_ONLY)
	public String getMavlinkId() {
		return sysId + "-" + compId + "v1";
	}
	
}
