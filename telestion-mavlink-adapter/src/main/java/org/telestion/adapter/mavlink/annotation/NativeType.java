package org.telestion.adapter.mavlink.annotation;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public enum NativeType {
	INT_8(1),
	UINT_8(1),
	INT_16(2),
	UINT_16(2),
	INT_32(4),
	UINT_32(4),
	INT_64(8),
	UINT_64(8),
	FLOAT(4),
	DOUBLE(8);
	
	public final int size;
	
	private NativeType(int size) {
		this.size = size;
	}
}
