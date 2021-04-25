package org.telestion.api.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

public class JsonMessageTest {

	@Test
	void testOn() {
		TestMessage msg = new TestMessage();
		assertThat(JsonMessage.on(TestMessage.class, msg.json(), m -> {
			assertThat(m, is(msg));
		}), is(true));
		assertThat(JsonMessage.on(JsonMessage.class, msg.json(), m -> {
			assertThat(m, is(msg));
		}), is(true));
	}

		private static record TestMessage(@JsonProperty float param1) implements JsonMessage {
		private TestMessage() {
			this(0);
		}
	}

}
