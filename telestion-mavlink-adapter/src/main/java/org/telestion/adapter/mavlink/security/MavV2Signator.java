package org.telestion.adapter.mavlink.security;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.bouncycastle.util.Arrays;

import com.google.common.base.Charsets;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public class MavV2Signator {
	
	/**
	 * 
	 */
	private static final long secondJan2015 = OffsetDateTime.of(2015, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toEpochSecond();
	
	/**
	 * 
	 * @return
	 */
	public static byte[] getTimestamp() {
		OffsetDateTime dt = OffsetDateTime.now(ZoneOffset.UTC);
		long time = ((dt.toEpochSecond() - secondJan2015) * 1_000_000 + (int) dt.toInstant().getNano() / 1_000) / 10;
		
		byte[] timestamp = {
				(byte) (time >>	40 	& 0xff),
				(byte) (time >>	32 	& 0xff),
				(byte) (time >>	24 	& 0xff),
				(byte) (time >>	16 	& 0xff),
				(byte) (time >>	8	& 0xff),
				(byte) (time		& 0xff),
		};
		
		return timestamp;
	}
	
	/**
	 * 
	 * @param linkId
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] rawSignature(String secretKey, byte[] header, byte[] payload, byte crcExtra, byte linkId,
			byte[] timestamp) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		ByteBuffer buffer = ByteBuffer.allocate(secretKey.length() + header.length + payload.length + 2
				+ timestamp.length);
		
		buffer.put(secretKey.getBytes(Charsets.US_ASCII));
		buffer.put(header);
		buffer.put(payload);
		buffer.put(crcExtra);
		buffer.put(linkId);
		buffer.put(timestamp);
		
		return Arrays.copyOfRange(digest.digest(buffer.array()), 0, 6);
	}
	
	/**
	 * 
	 * @param linkId
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] generateSignature(String secretKey, byte[] header, byte[] payload, byte crcExtra, byte linkId)
			throws NoSuchAlgorithmException {
		byte[] timestamp = getTimestamp();
		
		ByteBuffer buffer = ByteBuffer.allocate(13);
		
		buffer.put(linkId);
		buffer.put(timestamp);
		buffer.put(rawSignature(secretKey, header, payload, crcExtra, linkId, timestamp));
		return null;
	}
	
}
