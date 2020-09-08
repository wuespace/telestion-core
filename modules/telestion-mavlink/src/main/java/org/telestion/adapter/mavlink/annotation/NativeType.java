package org.telestion.adapter.mavlink.annotation;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public enum NativeType {
	INT_8(1, false),
	UINT_8(1, true),
	INT_16(2, false),
	UINT_16(2, true),
	INT_32(4, false),
	UINT_32(4, true),
	INT_64(8, false),
	UINT_64(8, true),
	FLOAT(4, false),
	DOUBLE(8, false),
	CHAR(1, false);
	
	public final int size;
	public final boolean unsigned;
	
	private NativeType(int size, boolean unsigned) {
		this.size = size;
		this.unsigned = unsigned;
	}
}
