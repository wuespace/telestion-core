package org.telestion.adapter.mavlink.message.internal;

import org.telestion.api.message.JsonMessage;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 *
 * @author Cedric Boes
 * @version 1.0
 */
@SuppressWarnings("preview")
public final record MavConnection(
		byte[] bytes,
		String remoteAddress) implements JsonMessage {
	
}
