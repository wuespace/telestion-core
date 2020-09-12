package org.telestion.adapter.mavlink.exception;

/**
 * A custom implementation of the {@link RuntimeException}.</br>
 * Indicates that the X.25 checksum of a MAVLink-Message is invalid.
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see RuntimeException
 */
public class InvalidChecksumException extends RuntimeException {

	/**
	 * SerialVersion UID for v1.0 of the {@link InvalidChecksumException}.
	 */
	private static final long serialVersionUID = 5920137939040604788L;
	
	/**
	 * {@inheritDoc}
	 */
	public InvalidChecksumException() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InvalidChecksumException(String s) {
		super(s);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InvalidChecksumException(Throwable t) {
		super(t);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InvalidChecksumException(String s, Throwable t) {
		super(s, t);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InvalidChecksumException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
