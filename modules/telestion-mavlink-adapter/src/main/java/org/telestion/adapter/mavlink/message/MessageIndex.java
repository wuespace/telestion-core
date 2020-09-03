package org.telestion.adapter.mavlink.message;

import java.util.HashMap;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class MessageIndex {
	/**
	 * 
	 */
	private static HashMap<Long, Class<? extends MavlinkMessage>> map = new HashMap<>();
	
	/**
	 * There shall be no objects!
	 */
	private MessageIndex() throws InstantiationException {
		throw new InstantiationException("There shall be no MessageIndex Object!");
	}
	
	/**
	 * 
	 * @param id
	 * @param class1
	 */
	public static void put(long id, Class<? extends MavlinkMessage> class1) {
		map.put(id, class1);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static Class<? extends MavlinkMessage> get(long id) {
		return map.get(id);
	}
}
