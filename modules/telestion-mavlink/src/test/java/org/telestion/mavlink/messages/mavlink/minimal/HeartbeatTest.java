package org.telestion.mavlink.messages.mavlink.minimal;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class HeartbeatTest {

    @Test public void testExist(){
        var msg = new Heartbeat(0, 0, 0, 0, 0, 0);
    }

    @Test public void testCrc(){
        var msg = new Heartbeat(0, 0, 0, 0, 0, 0);
        assertThat(msg.getCrc(), is(50));
    }

}
