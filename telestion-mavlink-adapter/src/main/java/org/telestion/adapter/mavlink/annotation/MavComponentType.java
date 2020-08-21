package org.telestion.adapter.mavlink.annotation;

/**
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see MavComponentInfo
 *
 */
public enum MavComponentType {
	/**
	 * 
	 */
	BYTE(1, byte.class),
	/**
	 * Int-8 Datatype Representation
	 */
	INT_8(1, byte.class),
	/**
	 * 
	 */
	UNSIGNED_INT_8(1, byte.class),
	/**
	 * 
	 */
	CHAR(1, char.class),
	/**
	 * 
	 */
	INT_16(2, short.class),
	/*
	 * 
	 */
	UNSIGNED_INT_16(2, short.class),
	/**
	 * 
	 */
	INT_32(4, int.class),
	/**
	 * 
	 */
	UNSIGNED_INT_32(4, int.class),
	/**
	 * Byte-Array which is already in the right format for Mavlink.
	 */
	RAW(-1, byte[].class);	// Jan v. P. this is what I meant with not throwing an Exception but just returning -1 ;)
	
	private final int size;
	private final Class<?> type;
	
	private MavComponentType(int size, Class<?> type) {
		this.size = size;
		this.type = type;
	}
	
	public int size() {
		return size;
	}
	
	public Class<?> type() {
		return type;
	}
}
