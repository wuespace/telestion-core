package org.telestion.adapter.mavlink.security;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * https://github.com/mavlink/c_library_v2/blob/master/checksum.h
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class X25Checksum {
	
	/**
	 * 
	 */
	public static final int INIT_CRC = 0xffff;

	/**
	 * There shall be no objects!
	 */
	private X25Checksum() {}
	
	/**
	 * 
	 * @param data
	 * @param currentCrc
	 * @return
	 */
	public static int calculate(int data, int currentCrc) {
		data ^= (byte) (currentCrc & 0xff);
		data ^= (data << 4);
		data &= 0xff;
		return ((currentCrc >> 8) ^ (data << 8) ^ (data << 3) ^ (data >> 4)) & 0xffff;
	}
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	public static int calculate(byte[] buffer) {
		int currentCrc = INIT_CRC;
		for (byte b : buffer) {
			currentCrc = calculate(b, currentCrc);
		}
		return currentCrc & 0xffff;
	}
}
