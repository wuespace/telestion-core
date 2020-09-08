package org.telestion.adapter.mavlink.exception;

/**
 * TODO: 
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public class WrongSignatureException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7162627217882841756L;

	public WrongSignatureException() {
		super();
	}

	public WrongSignatureException(String message) {
		super(message);
	}

	public WrongSignatureException(Throwable cause) {
		super(cause);
	}

	public WrongSignatureException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongSignatureException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
