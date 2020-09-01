package org.telestion.adapter.mavlink.exception;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public class InvalidChecksumException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5920137939040604788L;
	
	/**
	 * 
	 */
	public InvalidChecksumException() {
		super();
	}
	
	/**
	 * 
	 * @param s
	 */
	public InvalidChecksumException(String s) {
		super(s);
	}
	
	/**
	 * 
	 * @param t
	 */
	public InvalidChecksumException(Throwable t) {
		super(t);
	}
	
	/**
	 * 
	 * @param s
	 * @param t
	 */
	public InvalidChecksumException(String s, Throwable t) {
		super(s, t);
	}
	
}
