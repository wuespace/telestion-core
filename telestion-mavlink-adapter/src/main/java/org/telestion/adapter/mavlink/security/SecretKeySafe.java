package org.telestion.adapter.mavlink.security;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public class SecretKeySafe {
	
	private byte[] secretKey;
	private static SecretKeySafe instance;
	
	private SecretKeySafe(byte[] secretKey) {
		this.secretKey = secretKey;
	}
	
	/**
	 * 
	 * @return
	 */
	public byte[] getSecretKey() {
		return secretKey;
	}
	
	/**
	 * Clears the password from memory and runs the {@link System#gc() Garbage-Collector}.</br>
	 * This ensures security for passwords when deleting passwords.
	 */
	public void deleteKey() {
		for (int i = 0; i < secretKey.length; i++) {
			secretKey[i] = 0x0;
		}
		
		secretKey = null;
		System.gc();
	}
	
	/**
	 * 
	 * @return
	 */
	public static SecretKeySafe getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @param secretKey
	 */
	public void createNewInstance(byte[] secretKey) {
		deleteKey();
		instance = new SecretKeySafe(secretKey);
	}
}
