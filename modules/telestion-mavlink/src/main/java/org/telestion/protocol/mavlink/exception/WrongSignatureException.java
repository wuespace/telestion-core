package org.telestion.protocol.mavlink.exception;

/**
 * A custom implementation of the {@link RuntimeException}.<br>
 * Indicates that the signature of a MAVLink-Message is invalid.
 *
 * @author Cedric Boes
 * @version 1.0
 * @see RuntimeException
 */
public class WrongSignatureException extends RuntimeException {

    /**
     * SerialVersion UID for v1.0 of this {@link WrongSignatureException}.
     */
    private static final long serialVersionUID = 7162627217882841756L;

    /**
     * @see RuntimeException#RuntimeException()
     */
    public WrongSignatureException() {
        super();
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     */
    public WrongSignatureException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public WrongSignatureException(Throwable cause) {
        super(cause);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public WrongSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable, boolean, boolean)
     */
    public WrongSignatureException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
