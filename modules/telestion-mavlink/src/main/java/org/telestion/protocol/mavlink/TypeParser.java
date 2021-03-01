package org.telestion.protocol.mavlink;

public interface TypeParser<T> {
	public T parse(byte[] payload, int arraySize);
}
