package org.telestion.adapter.mavlink.message.internal;

import org.telestion.adapter.mavlink.message.RawPayload;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public record RawMavlinkV2(
		short len,
		short incompatFlags,
		short compatFlags,
		short seq,
		short sysId,
		short compId,
		long msgId,
		RawPayload payload,
		int checksum,
		byte[] signature) implements RawMavlink {

	@Override
	public String getMavlinkId() {
		return sysId + "-" + compId + "v2";
	}

}
