package org.telestion.adapter.mavlink.exception;

import java.io.Serial;

import org.telestion.adapter.mavlink.message.MavlinkMessage;

/**
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see RuntimeException
 * @see MavlinkMessage
 *
 */
public class InvalidMavlinkMessageException extends RuntimeException {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = -3903047833257664877L;
	
	/**
	 * 
	 */
	public InvalidMavlinkMessageException() {
		super();
	}
	
	/**
	 * 
	 * @param cause
	 */
	public InvalidMavlinkMessageException(String cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param t
	 */
	public InvalidMavlinkMessageException(Throwable t) {
		super(t);
	}
	
	/**
	 * 
	 * @param cause
	 * @param t
	 */
	public InvalidMavlinkMessageException(String cause, Throwable t) {
		super(cause, t);
	}

}
