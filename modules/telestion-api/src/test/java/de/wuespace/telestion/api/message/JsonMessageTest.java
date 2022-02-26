package de.wuespace.telestion.api.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

public class JsonMessageTest {

	public record TestMessage(@JsonProperty float param1) implements JsonMessage {
		public TestMessage() {
			this(0);
		}
	}

	@Test
	void testOn() {
		TestMessage msg = new TestMessage();

		assertThat(JsonMessage.on(TestMessage.class, msg.toJsonObject(), m -> {
			assertThat(m, is(msg));
		}), is(true));
	}
}
