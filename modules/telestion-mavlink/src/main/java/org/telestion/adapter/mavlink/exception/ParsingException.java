package org.telestion.adapter.mavlink.exception;

/**
 * A custom implementation of the {@link RuntimeException}.</br>
 * Indicates that something went wrong in the parsing-process of a MAVLink-Message.
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see RuntimeException
 */
public class ParsingException extends RuntimeException {

	/**
	 * SerialVersion UID for v1.0 of this {@link ParsingException}.
	 */
	private static final long serialVersionUID = -7127168456897713093L;

	/**
	 * {@inheritDoc}
	 */
	public ParsingException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public ParsingException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public ParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public ParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
