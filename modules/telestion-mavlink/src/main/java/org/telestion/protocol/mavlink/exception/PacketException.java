package org.telestion.protocol.mavlink.exception;

/**
 * A custom implementation of the {@link RuntimeException}.</br>
 * Indicates that something went wrong with the MAVLink-Message-packet (e.g. wrong format).
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see RuntimeException
 */
public class PacketException extends RuntimeException {
	
	/**
	 * SerialVersion UID for v1.0 of this {@link PacketException}.
	 */
	private static final long serialVersionUID = 2256065887188465994L;
	
	/**
	 * {@inheritDoc}
	 */
	public PacketException() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PacketException(String s) {
		super(s);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PacketException(Throwable t) {
		super(t);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PacketException(String s, Throwable t) {
		super(s, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public PacketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
