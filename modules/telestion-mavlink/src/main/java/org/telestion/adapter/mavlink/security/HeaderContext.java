package org.telestion.adapter.mavlink.security;

import org.telestion.adapter.mavlink.MavlinkParser;

/**
 * Containing all relevant header-information for the {@link MavlinkParser}.</br>
 * The {@link HeaderContext} is MAVLinkV1- and MAVLinkV2-compatible which means that both formats can put out
 * using this.
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class HeaderContext {
	/**
	 * 
	 */
	private volatile long packetId;
	/**
	 * 
	 */
	private final short incompFlags;
	/**
	 * 
	 */
	private final short compFlags;
	/**
	 * 
	 */
	private final short seq;
	/**
	 * 
	 */
	private final short sysId;
	/**
	 * 
	 */
	private final short compId;
	
	/**
	 * 
	 */
	public HeaderContext(short incompFlags, short compFlags, short seq, short sysId, short compId) {
		this.incompFlags = incompFlags;
		this.compFlags = compFlags;
		this.seq = seq;
		this.sysId = sysId;
		this.compId = compId;
		this.packetId = 0;
	}

	/**
	 * 
	 * @return
	 */
	public synchronized long getNewMessageId() {
		return packetId++;
	}

	/**
	 * 
	 * @return
	 */
	public short incompFlags() {
		return incompFlags;
	}

	/**
	 * 
	 * @return
	 */
	public short compFlags() {
		return compFlags;
	}

	/**
	 * 
	 * @return
	 */
	public short seq() {
		return seq;
	}

	/**
	 * 
	 * @return
	 */
	public short sysId() {
		return sysId;
	}

	/**
	 * 
	 * @return
	 */
	public short compId() {
		return compId;
	}
	
}
