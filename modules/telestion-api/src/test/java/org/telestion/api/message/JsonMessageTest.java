package org.telestion.api.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JsonMessageTest {

    @SuppressWarnings("preview")
	private static record TestMessage(@JsonProperty float param1) implements JsonMessage {
        private TestMessage() {
            this(0);
        }
    }

    @Test void testOn() {
        TestMessage msg = new TestMessage();
        assertThat(JsonMessage.on(TestMessage.class, msg.json(), m -> {
            assertThat(m, is(msg));
        }), is(true));
        assertThat(JsonMessage.on(JsonMessage.class, msg.json(), m -> {
            assertThat(m, is(msg));
        }), is(true));
    }

}
