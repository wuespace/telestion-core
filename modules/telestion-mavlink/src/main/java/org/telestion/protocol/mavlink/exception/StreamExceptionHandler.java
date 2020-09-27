package org.telestion.protocol.mavlink.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Cedric Boes
 *
 * @param <T> Throwable which should be stored in this safe.
 */
public class StreamExceptionHandler<T extends Throwable> {
	
	private volatile List<T> list;
	
	public StreamExceptionHandler() {
		list = Collections.synchronizedList(new ArrayList<T>());
	}
	
	public synchronized void put(T throwable) {
		this.list.add(throwable);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized T[] get() {
		return (T[]) list.toArray(Throwable[]::new);
	}
	
	public synchronized boolean isEmpty() {
		return this.list.isEmpty();
	}
	
}
