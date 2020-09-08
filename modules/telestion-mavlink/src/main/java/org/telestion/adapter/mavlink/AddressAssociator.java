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
	private static final HashMap<String, AddressPort> mapping = new HashMap<>();
	
	/**
	 * 
	 * @param mavlinkAddress
	 * @param ip
	 */
	public static void put(String mavlinkAddress, AddressPort ip) {
		mapping.put(mavlinkAddress, ip);
	}
	
	/**
	 * 
	 * @param mavlinkAddress
	 * @return
	 */
	public static AddressPort remove(String mavlinkAddress) {
		return mapping.remove(mavlinkAddress);
	}
}
