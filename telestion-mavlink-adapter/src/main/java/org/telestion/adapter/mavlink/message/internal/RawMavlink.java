package org.telestion.adapter.mavlink.message.internal;

import org.telestion.api.message.JsonMessage;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public interface RawMavlink extends JsonMessage {
	/**
	 * 
	 * @return
	 */
	public String getMavlinkId();
}
