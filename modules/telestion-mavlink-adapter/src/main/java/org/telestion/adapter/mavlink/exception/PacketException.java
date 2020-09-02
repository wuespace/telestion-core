package org.telestion.adapter.mavlink.exception;

/**
 * TODO: Java-Docs to make @pklaschka happy
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see RuntimeException
 */
public class PacketException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2256065887188465994L;
	
	/**
	 * 
	 */
	public PacketException() {
		super();
	}
	
	/**
	 * 
	 * @param s
	 */
	public PacketException(String s) {
		super(s);
	}
	
	/**
	 * 
	 * @param t
	 */
	public PacketException(Throwable t) {
		super(t);
	}
	
	/**
	 * 
	 * @param s
	 * @param t
	 */
	public PacketException(String s, Throwable t) {
		super(s, t);
	}
	
}
