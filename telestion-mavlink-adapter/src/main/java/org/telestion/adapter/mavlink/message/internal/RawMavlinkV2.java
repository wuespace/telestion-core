package org.telestion.adapter.mavlink.message.internal;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.1
 */
@SuppressWarnings("preview")
public record RawMavlinkV2(
		@JsonProperty short len,
		@JsonProperty short incompatFlags,
		@JsonProperty short compatFlags,
		@JsonProperty short seq,
		@JsonProperty short sysId,
		@JsonProperty short compId,
		@JsonProperty long msgId,
		@JsonProperty RawPayload payload,
		@JsonProperty int checksum,
		@JsonProperty short linkId,
		@JsonProperty long timestamp,
		@JsonProperty long signature) implements RawMavlink {
	
	@SuppressWarnings("unused")
	private RawMavlinkV2() {
		this((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, 0l, null, 0, (short) 0, 0l, 0l);
	}
	
	public RawMavlinkV2(byte[] bytes) {
		this(	(short) bytes[1],
				(short) bytes[2],
				(short) bytes[3],
				(short) bytes[4],
				(short) bytes[5],
				(short) bytes[6],
				(long) 	bytes[7] << 16 + (bytes[8] << 8) + bytes[9],
				new RawPayload(Arrays.copyOfRange(bytes, 10, bytes[1] + 10)),
				(int)	bytes[bytes[1] + 10] << 8 + bytes[bytes[1] + 11],
				(short) bytes[2] == 0x1 ? bytes[bytes[1] + 12] : 0,
				(long) 	bytes[2] == 0x1 ? 
									bytes[bytes[1] + 13] << 40l + bytes[bytes[1] + 14] << 32 +
									bytes[bytes[1] + 15] << 24l + bytes[bytes[1] + 16] << 16 + 
									bytes[bytes[1] + 17] << 8l + bytes[bytes[1] + 18]
								: 0,
				(long) 	bytes[2] == 0x1 ? 
						bytes[bytes[1] + 19] << 40l + bytes[bytes[1] + 20] << 32 +
						bytes[bytes[1] + 21] << 24l + bytes[bytes[1] + 22] << 16 + 
						bytes[bytes[1] + 23] << 8l + bytes[bytes[1] + 24]
					: 0);
	}
	
	@Override
	@JsonProperty(access = Access.READ_ONLY)
	public String getMavlinkId() {
		return sysId + "-" + compId + "v2";
	}

}
