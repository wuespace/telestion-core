package org.telestion.protocol.mavlink.messages.minimal;

import org.junit.jupiter.api.Test;
import org.telestion.protocol.mavlink.messages.official.minimal.Heartbeat;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class HeartbeatTest {

	@Test
	void testExist() {
		assertDoesNotThrow(() -> Class.forName("org.telestion.protocol.mavlink.messages.official.minimal.Heartbeat")
				.getConstructor(long.class, int.class, int.class, int.class, int.class, int.class)
				.newInstance(0, 0, 0, 0, 0, 0));
	}

	@Test
	void testCrc() {
		var msg = new Heartbeat(0, 0, 0, 0, 0, 0);
		assertThat(msg.getCrc(), is(50));
	}

}
