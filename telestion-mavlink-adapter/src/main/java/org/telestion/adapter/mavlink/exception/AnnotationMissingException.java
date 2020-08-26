package org.telestion.adapter.mavlink.exception;

/**
 * TODO: Java-Docs to make @pklaschka happy
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see RuntimeException
 */
public class AnnotationMissingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342016963944609412L;
	
	/**
	 * 
	 */
	public AnnotationMissingException() {
		super();
	}
	
	/**
	 * 
	 * @param s
	 */
	public AnnotationMissingException(String s) {
		super(s);
	}
	
	/**
	 * 
	 * @param t
	 */
	public AnnotationMissingException(Throwable t) {
		super(t);
	}
	
	/**
	 * 
	 * @param s
	 * @param t
	 */
	public AnnotationMissingException(String s, Throwable t) {
		super(s, t);
	}
	
}
