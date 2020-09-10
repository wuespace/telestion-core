package org.telestion.mavlink.messages.mavlink.minimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class HeartbeatTest {

    @Test void testExist(){
		assertDoesNotThrow(() -> Class.forName("org.telestion.mavlink.messages.mavlink.minimal.Heartbeat")
				.getConstructor(int.class, int.class, int.class, long.class, int.class, int.class)
				.newInstance(0, 0, 0, 0, 0, 0));
    }

    @Test void testCrc(){
        var msg = new Heartbeat(0, 0, 0, 0, 0, 0);
        assertThat(msg.getCrc(), is(50));
    }

}
