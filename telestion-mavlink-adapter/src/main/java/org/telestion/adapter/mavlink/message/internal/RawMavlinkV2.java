package org.telestion.adapter.mavlink.message.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
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
		@JsonProperty byte[] signature) implements RawMavlink {
	
	@SuppressWarnings("unused")
	private RawMavlinkV2() {
		this((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, 0l, null, 0, null);
	}
	
	@Override
	@JsonProperty(access = Access.READ_ONLY)
	public String getMavlinkId() {
		return sysId + "-" + compId + "v2";
	}

}
