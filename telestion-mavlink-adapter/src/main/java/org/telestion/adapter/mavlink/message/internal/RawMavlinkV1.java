package org.telestion.adapter.mavlink.message.internal;

import org.telestion.adapter.mavlink.message.RawPayload;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public record RawMavlinkV1(
		short len,
		short seq,
		short sysId,
		short compId,
		short msgId,
		RawPayload payload,
		int checksum)implements RawMavlink {

	@Override
	public String getMavlinkId() {
		return sysId + "-" + compId + "v1";
	}
	
}
