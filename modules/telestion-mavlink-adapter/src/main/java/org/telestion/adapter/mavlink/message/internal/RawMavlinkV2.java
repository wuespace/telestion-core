package org.telestion.adapter.mavlink.message.internal;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

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
		@JsonProperty byte[] signature) implements RawMavlink {
	
	@SuppressWarnings("unused")
	private RawMavlinkV2() {
		this((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, 0l, null, 0, (short) 0, null);
	}
	
	public RawMavlinkV2(byte[] bytes) {
		this(	(short) bytes[1],
				(short) bytes[2],
				(short) bytes[3],
				(short) bytes[4],
				(short) bytes[5],
				(short) bytes[6],
				(long) 	(bytes[7] << 16) + (bytes[8] << 8) + bytes[9],
				new RawPayload(Arrays.copyOfRange(bytes, 10, bytes[1] + 10)),
				(int)	(bytes[bytes[1] + 10] << 8) + bytes[bytes[1] + 11],
				(short) (bytes[2] & 0x1) == 0x1 ? bytes[bytes[1] + 12] : 0,
				(byte[])((bytes[2] & 0x1) == 0x1 ? Arrays.copyOfRange(bytes, 13, 25)
								: new byte[] {}));
	}
	
	@Override
	public String getMavlinkId() {
		return sysId + "-" + compId + "v2";
	}
	
	@Override
	public byte[] getRaw() {
		ByteBuffer raw = ByteBuffer.allocate(12 + payload.payload().length);
		
		raw.put((byte) 0xFD);
		raw.put((byte) (len				&	0xff));
		raw.put((byte) (incompatFlags	&	0xff));
		raw.put((byte) (compatFlags		&	0xff));
		raw.put((byte) (seq				&	0xff));
		raw.put((byte) (sysId			&	0xff));
		raw.put((byte) (compId			&	0xff));
		raw.put((byte) ((msgId	>> 16)	&	0xff));
		raw.put((byte) ((msgId	>> 8) 	&	0xff));
		raw.put((byte) (msgId			&	0xff));
		raw.put(payload.payload());
		raw.put((byte) ((checksum >> 8)	&	0xff));
		raw.put((byte) (checksum		&	0xff));
		
		return raw.array();
	}

}
