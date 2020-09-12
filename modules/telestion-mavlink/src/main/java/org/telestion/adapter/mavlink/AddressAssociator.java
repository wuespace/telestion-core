package org.telestion.adapter.mavlink;

import java.util.HashMap;

/**
 * Handling the linking processes between the MAVLink-addresses and the ip-address of the {@link AddressPort}.</br>
 * Before being able to get the messages by id they must be registered here.</br>
 * </br>
 * <em>Note that this class should be used in a static context.</em></br>
 * </br>
 * THIS IS ONLY READY FOR TCP-CONNECTIONS, YET! (I know I am yelling @jvpichowski :D)
 *  
 * @author Cedric Boes
 * @version 1.0
 */
public final class AddressAssociator {

	/**
	 * Actual Map for the linking.
	 */
	private static final HashMap<String, AddressPort> map = new HashMap<>();
	
	/**
	 * There shall be no objects!
	 */
	private AddressAssociator() throws InstantiationException {
		throw new InstantiationException("There shall be no MessageIndex Object!");
	}
	
	/**
	 * Registers a new item by linking a new MAVLink-address to a {@link AddressPort}.</br>
	 * Basically {@link HashMap#put(Object, Object)}.</br>
	 * </br>
	 * <em>Note that if an other {@link AddressPort} has already been linked with the given mavlinkAddress an will 
	 * {@link IllegalArgumentException} be thrown.</em>
	 * 
	 * @param mavlinkAddress of the new {@link AddressPort}
	 * @param ip which should be linked
	 * @throws IllegalArgumentException if the given id is already in use
	 */
	public static void put(String mavlinkAddress, AddressPort ip) {
		if (map.get(mavlinkAddress) != null) {
			throw new IllegalArgumentException("Given ID is already in use!");
		}
		map.put(mavlinkAddress, ip);
	}
	
	/**
	 * Returns the {@link AddressPort} linked to the given mavlinkAddress and removes it from the {@link #map}.</br>
	 * Basically {@link HashMap#get(Object)}.</br>
	 * </br>
	 * <em>Note that if the given id is not registered <code>null</code> will be returned.</em>
	 * 
	 * @param mavlinkAddress of the new {@link AddressPort}
	 * @return {@link AddressPort} linked to the given mavlinkAddress
	 */
	public static AddressPort remove(String mavlinkAddress) {
		return map.remove(mavlinkAddress);
	}
}
