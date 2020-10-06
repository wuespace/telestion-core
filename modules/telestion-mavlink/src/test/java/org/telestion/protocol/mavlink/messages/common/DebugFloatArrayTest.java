package org.telestion.protocol.mavlink.messages.common;

import org.junit.jupiter.api.Test;
import org.telestion.protocol.mavlink.messages.official.common.DebugFloatArray;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DebugFloatArrayTest {

	@Test
	void testExist() {
		assertDoesNotThrow(
				() -> Class.forName("org.telestion.protocol.mavlink.messages.official.common" + ".DebugFloatArray")
						.getConstructor(long.class, int.class, int[].class, int[].class).newInstance(0, 0, null, null));
	}

	@Test
	void testCrc() {
		var msg = new DebugFloatArray(0, 0, null, null);
		assertThat(msg.getCrc(), is(232));
	}

}
