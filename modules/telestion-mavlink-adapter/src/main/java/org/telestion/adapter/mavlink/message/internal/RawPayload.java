package org.telestion.adapter.mavlink.message.internal;

import org.telestion.api.message.JsonMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public final record RawPayload(
		@JsonProperty byte[] payload) implements JsonMessage {
	
	@SuppressWarnings("unused")
	private RawPayload() {
		this(null);
	}
}
