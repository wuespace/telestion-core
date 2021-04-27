package de.wuespace.telestion.services.util;

import java.util.function.Predicate;

/**
 * A UnsafePredicate is a {@link Predicate} which allows exceptions to be thrown.
 *
 * @param <T> the type of the predicate
 */
@FunctionalInterface
public interface UnsafePredicate<T> {

	/**
	 * Wraps an unsafe predicate into a safe one.
	 *
	 * @param pred the predicate to be wrapped
	 * @param <T>  the type of the predicate
	 * @return the safe predicate
	 */
	static <T> Predicate<T> safe(UnsafePredicate<T> pred) {
		return t -> {
			try {
				return pred.test(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	/**
	 * Same as {@link Predicate#test(Object)}.
	 *
	 * @param t the value to be tested
	 * @return true if the predicate is valid
	 * @throws Exception could potentially throw any exception
	 */
	boolean test(T t) throws Exception;
}
