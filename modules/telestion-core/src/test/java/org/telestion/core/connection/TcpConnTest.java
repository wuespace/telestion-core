package org.telestion.core.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.telestion.api.message.JsonMessage;

@ExtendWith(VertxExtension.class)
public class TcpConnTest {

	@Test
	void roundTripTest(Vertx vertx, VertxTestContext testContext) throws Throwable {
		// the server publishes all received data to this address on the event bus
		var serverOutgoing = "serverOutgoing";

		// the server sends all on this event bus address consumed data over tcp
		var serverIncoming = "serverIncoming";

		// the client publishes all received data to this address on the event bus
		var clientOutgoing = "clientOutgoing";

		// the client sends all on this event bus address consumed data over tcp
		var clientIncoming = "clientIncoming";

		vertx.eventBus().consumer(clientOutgoing, msg -> {
			JsonMessage.on(TcpConn.Data.class, msg, data -> {
				// send message back
				vertx.eventBus().publish(clientIncoming, data.json());
			});
		});
		vertx.eventBus().consumer(serverOutgoing, msg -> {
			var bytes = new byte[] { 2, 4, 5, 3 };
			JsonMessage.on(TcpConn.Participant.class, msg, participant -> {
				// send message on connection
				vertx.eventBus().publish(serverIncoming, new TcpConn.Data(participant, bytes).json());
			});
			JsonMessage.on(TcpConn.Data.class, msg, data -> {
				// test message
				assertThat(data.data(), is(bytes));
				testContext.completeNow();
			});
		});

		var server = new TcpConn(null, 23654, serverOutgoing, null, Collections.singletonList(serverIncoming));
		var client = new TcpConn("127.0.0.1", 23654, clientOutgoing, null, Collections.singletonList(clientIncoming));

		vertx.deployVerticle(server, result -> {
			if (result.succeeded()) {
				vertx.deployVerticle(client);
			}
		});

		assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
	}
}
