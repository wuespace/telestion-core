package org.telestion.adapter.mavlink.message.internal;

import org.telestion.api.message.JsonMessage;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public final record RawPayload(
		byte[] payload) implements JsonMessage {
	
	@SuppressWarnings("unused")
	private RawPayload() {
		this(null);
	}
}
