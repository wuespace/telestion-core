package org.telestion.adapter.mavlink.message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.telestion.adapter.mavlink.annotation.MavComponentInfo;
import org.telestion.adapter.mavlink.annotation.MavComponentType;
import org.telestion.adapter.mavlink.exception.InvalidMavlinkMessageException;

/**
 * 
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 *
 */
public class Parser {
	/**
	 * 
	 */
	private static final Map<Class<?>, Handler> dispatch = new HashMap<Class<?>, Handler>();
	
	static {
		dispatch.put(byte.class, (o, b) -> {
			b.put((byte) o);
		});
		
		dispatch.put(byte[].class, (o, b) -> {
			for (byte b1 : (byte[]) o)
				dispatch.get(byte.class).handle(b1, b);
		});
		
		dispatch.put(short.class, (o, b) -> {
			b.putShort((short) o);
		});
		
		dispatch.put(long[].class, (o, b) -> {
			for (short s : (short[]) o)
				dispatch.get(short.class).handle(s, b);
		});
		
		dispatch.put(int.class, (o, b) -> {
			b.putInt((int) o);
		});
		
		dispatch.put(int[].class, (o, b) -> {
			for (int i : (int[]) o)
				dispatch.get(int.class).handle(i, b);
		});
		
		dispatch.put(long.class, (o, b) -> {
			b.putLong((long) o);
		});
		
		dispatch.put(long[].class, (o, b) -> {
			for (long l : (long[]) o)
				dispatch.get(long.class).handle(l, b);
		});
		
		dispatch.put(float.class, (o, b) -> {
			b.putFloat((float) o);
		});
		
		dispatch.put(float[].class, (o, b) -> {
			for (float f : (float[]) o)
				dispatch.get(float.class).handle(f, b);
		});
		
		dispatch.put(double.class, (o, b) -> {
			b.putDouble((double) o);
		});
		
		dispatch.put(double[].class, (o, b) -> {
			for (double d : (double[]) o)
				dispatch.get(double.class).handle(d, b);
		});
		
		dispatch.put(boolean.class, (o, b) -> {
			b.put((boolean) o ? (byte) 1 : (byte) 0);
		});
		
		dispatch.put(boolean[].class, (o, b) -> {
			for (boolean b1 : (boolean[]) o)
				dispatch.get(boolean.class).handle(b1, b);
		});
		
		dispatch.put(char.class, (o, b) -> {
			b.putChar((char) o);
		});
		
		dispatch.put(char[].class, (o, b) -> {
			for (char c : (char[]) o)
				dispatch.get(char.class).handle(c, b);
		});
		
		dispatch.put(String.class, (o, b) -> {
			dispatch.get(char[].class).handle(((String) o).toCharArray(), b);
		});
		
	}
	
	/**
	 * 
	 * @author Cedric Boes
	 * @version 1.0
	 * @see Parser
	 *
	 */
	@FunctionalInterface
	static interface Handler {
		void handle(Object o, ByteBuffer b);
	}
	
	/**
	 * 
	 * @param obj
	 */
	public static void parse(MavlinkMessage obj) {		
		var clazz = obj.getClass();
		
		var components = clazz.getRecordComponents();
		
		// CHECK IF CLASS IS VALID
		// This means it is not a record or it is useless -> no valid MavlinkMessage...
		if (components.length == 0)
			throw new InvalidMavlinkMessageException("Given MavlinkMessage is not a record or does not consist of any "
					+ "elements!");
		
		try {
			// CHECK IF ANNOTATION IS SET AND BRING INTO RIGHT ORDER
			components = Arrays.stream(components).sorted((r1, r2) -> {
							if (!r1.isAnnotationPresent(MavComponentInfo.class) &&
									r1.isAnnotationPresent(MavComponentInfo.class))
								throw new InvalidMavlinkMessageException("Given MavlinkMessage does have at least one "
										+ "RecordComponent without the necessary MavComponentInfo annotation!");
							return r1.getAnnotation(MavComponentInfo.class).position()
									- r2.getAnnotation(MavComponentInfo.class).position();
						}).toArray(RecordComponent[]::new);
			
			// INIT BUFFER
			var bufferSize = 0;

			for (RecordComponent rc : components) {
				var an = rc.getAnnotation(MavComponentInfo.class);
				
				if (an.type() == MavComponentType.RAW) {
					// If in later revisions multiple JArray-Types are supported, change here...
					bufferSize += ((byte[]) rc.getAccessor().invoke(obj)).length;
				} else {
					bufferSize += an.type().size();
				}
			}
			
			var buffer = ByteBuffer.allocate(bufferSize);
			
			// WRITE TO BUFFER
			for (RecordComponent rc : components) {
				Object o = rc.getAccessor().invoke(obj);
				MavComponentType mc = rc.getAnnotation(MavComponentInfo.class).type();
				
//				if (!mc.type().isInstance(o))	// TODO !!!!!!!! You can do this as well Jan v. P. ;)
//					o = mc.getClass().o.getClass().cast(o).toString();
				
				dispatch.get(rc.getClass()).handle(o, buffer);
			}
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// Wrapping old Exception into an unchecked one
			throw new InvalidMavlinkMessageException(e);
		}
	}
}
