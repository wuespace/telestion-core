package org.telestion.adapter.mavlink;

import java.util.HashMap;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class AddressAssociator {

	/**
	 * 
	 */
	private static final HashMap<String, String> mapping = new HashMap<>();
	
	/**
	 * 
	 * @param mavlinkAddress
	 * @param ip
	 */
	public static void put(String mavlinkAddress, String ip) {
		mapping.put(mavlinkAddress, ip);
	}
	
	/**
	 * 
	 * @param mavlinkAddress
	 * @return
	 */
	public static String remove(String mavlinkAddress) {
		return mapping.remove(mavlinkAddress);
	}
}
