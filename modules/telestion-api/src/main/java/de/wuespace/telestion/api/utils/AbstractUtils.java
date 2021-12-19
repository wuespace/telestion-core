package de.wuespace.telestion.api.utils;

/**
 * When class extends AbstractUtils it indicates that the extending class is a utility class which can not be
 * instantiated. There should be no callable constructors which is why the default constructor is also blocked.
 *
 * @author Cedric Boes, Ludwig Richter
 */
public abstract class AbstractUtils {
	/**
	 * There shall be no objects of a Utility class. This is why this constructor throws an
	 * {@link UnsupportedOperationException}.
	 *
	 * @throws UnsupportedOperationException Will always be thrown because there shall be no objects of a utility class
	 */
	// Utility classes do not have objects
	protected AbstractUtils() throws UnsupportedOperationException {
		var className = this.getClass().getName();
		var errorMsg = String.format("""
				There shall be no objects of a utility class!
				%s extending AbstractUtils is automatically a utility class. It must not be instantiated.""",
				className);
		throw new UnsupportedOperationException(errorMsg);
	}
}
