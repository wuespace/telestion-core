package org.telestion.adapter.mavlink.message.internal;

import org.telestion.api.message.JsonMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
	@JsonProperty(access = Access.READ_ONLY)
	public String getMavlinkId();
	
	/**
	 * 
	 * @return
	 */
	@JsonProperty(access = Access.READ_ONLY)
	public byte[] getRaw();
}
