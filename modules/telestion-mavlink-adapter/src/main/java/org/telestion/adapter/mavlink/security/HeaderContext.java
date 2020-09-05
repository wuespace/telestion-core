package org.telestion.adapter.mavlink.security;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class HeaderContext {
	private volatile long packetId;
	private final short incompFlags;
	private final short compFlags;
	private final short seq;
	private final short sysId;
	private final short compId;
	
	public HeaderContext(short incompFlags, short compFlags, short seq, short sysId, short compId) {
		this.incompFlags = incompFlags;
		this.compFlags = compFlags;
		this.seq = seq;
		this.sysId = sysId;
		this.compId = compId;
		this.packetId = 0;
	}

	public synchronized long getNewMessageId() {
		return packetId++;
	}

	public short incompFlags() {
		return incompFlags;
	}

	public short compFlags() {
		return compFlags;
	}

	public short seq() {
		return seq;
	}

	public short sysId() {
		return sysId;
	}

	public short compId() {
		return compId;
	}
	
	
}
