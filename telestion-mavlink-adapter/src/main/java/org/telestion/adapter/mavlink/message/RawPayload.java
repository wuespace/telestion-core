package org.telestion.adapter.mavlink.message;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public final record RawPayload(
		byte[] payload) implements MavlinkMessage {
	
	@SuppressWarnings("unused")
	private RawPayload() {
		this(null);
	}
}
