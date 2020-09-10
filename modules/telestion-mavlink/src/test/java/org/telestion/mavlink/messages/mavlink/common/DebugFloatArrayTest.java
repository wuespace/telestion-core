package org.telestion.mavlink.messages.mavlink.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class DebugFloatArrayTest {

    @Test void testExist(){
		assertDoesNotThrow(() -> Class.forName("org.telestion.mavlink.messages.mavlink.common.DebugFloatArray.class")
				.getConstructor(long.class, int[].class, int.class, int[].class)
				.newInstance(0, null, 0, null));
    }

    @Test void testCrc(){
        var msg = new DebugFloatArray(0, null, 0, null);
        assertThat(msg.getCrc(), is(232));
    }

}
