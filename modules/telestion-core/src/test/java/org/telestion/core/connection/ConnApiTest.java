package org.telestion.core.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ExtendWith(VertxExtension.class)
public class ConnApiTest {

	@Test
	void testReceiver(Vertx vertx, VertxTestContext testContext) throws Throwable {
		// Create test-data and constants
		var RECEIVER_COUNT = 10;
		var bytes = new byte[] {'T', 'E', 'S', 'T'};

		// Create various dummy Receiver-Addresses
		var addresses = IntStream.range(0, RECEIVER_COUNT).mapToObj(i -> "ReceiverInput" + i).toArray(String[]::new);

		// Output-Address for Receiver-Api
		var outputAddress = "ReceiverApiPublish";

		// Create listener for Api
		var queue = new ConcurrentLinkedQueue<Integer>();
		vertx.eventBus().consumer(outputAddress, raw -> JsonMessage.on(ConnectionData.class, raw, msg -> {
			if (msg.conDetails() instanceof TestConnDetails testDetails) {
				logger.info("Received data from {}", testDetails.receiverNumber());
				assertThat(msg.rawData(), is(bytes));
				queue.add(testDetails.receiverNumber());
				Supplier<Stream<Integer>> supplier = () -> queue.stream().distinct();
				if (supplier.get().allMatch(i -> i >= 0 && i <= RECEIVER_COUNT)
						&& supplier.get().toArray().length == RECEIVER_COUNT) {
					testContext.completeNow();
				}
			} else {
				logger.warn("Although this is a special Test-Case, other information were sent to the "
						+ "receiver-api");
			}
		}));

		// Create and deploy Receiver verticle (mustn't fail!)
		var receiver = new Receiver(new Receiver.Configuration(outputAddress, addresses));
		vertx.deployVerticle(receiver, result -> {
			if (result.failed()) {
				testContext.failNow(result.cause());
			}
		});

		// Publish Test data on bus
		logger.info("Sending Test-Data");
		IntStream.range(0, RECEIVER_COUNT).forEach(i -> vertx.eventBus().publish(addresses[i],
				new ConnectionData(bytes, new TestConnDetails(i)).json()));
		logger.info("All Test-Data sent");

		// Wait for completion
		assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
	}

	@Test
	void testSender(Vertx vertx, VertxTestContext testContext) throws Throwable {
		// Create test-data and constants
		var SENDER_COUNT = 10;
		var PACKAGE_COUNT = 10;
		var bytes = new byte[] {'T', 'E', 'S', 'T'};

		// Create various dummy Sender-Addresses
		var addresses = IntStream.range(0, SENDER_COUNT).mapToObj(i -> "SenderIncoming" + i).toArray(String[]::new);

		// Addresses for Sender-Api
		var inputAddress = "senderApiInput";

		// Create Listeners
		var queue = new ConcurrentLinkedQueue<Integer>();
		Arrays.stream(addresses).forEach(addr -> vertx.eventBus().consumer(addr,
				raw -> JsonMessage.on(SenderData.class, raw, msg -> {
			for (var detail : msg.conDetails()) {
				if (detail instanceof TestConnDetails testDetails) {
					logger.info("Received data from {}", testDetails.receiverNumber());
					assertThat(msg.rawData(), is(bytes));
					queue.add(testDetails.receiverNumber());
					Supplier<Stream<Integer>> supplier = () -> queue.stream().distinct();
					if (supplier.get().allMatch(i -> i >= 0 && i <= SENDER_COUNT)
							&& supplier.get().toArray().length == SENDER_COUNT) {
						testContext.completeNow();
					}
				} else {
					logger.warn("Although this is a special Test-Case, other information were sent to the "
							+ "sender-api");
				}
			}
		})));

		// Create and deploy Receiver verticle (mustn't fail!)
		var sender = new Sender(new Sender.Configuration(inputAddress, addresses));
		vertx.deployVerticle(sender, result -> {
			if (result.failed()) {
				testContext.failNow(result.cause());
			}
		});

		// Publish Test data on bus
		logger.info("Sending Test-Data");
		IntStream.range(0, PACKAGE_COUNT)
				.forEach(i -> vertx.eventBus().publish(inputAddress,
						new SenderData(bytes,
								IntStream.range(0, SENDER_COUNT).mapToObj(TestConnDetails::new)
										.toArray(TestConnDetails[]::new)).json()));
		logger.info("All Test-Data sent");

		// Wait for completion
		assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
	}

	@Test
	void testStaticSender(Vertx vertx, VertxTestContext testContext) {
		testContext.completeNow();
	}

	@Test
	void testRoundTrip(Vertx vertx, VertxTestContext testContext) {
		testContext.completeNow();
	}

	private record TestConnDetails(@JsonProperty int receiverNumber) implements ConnectionDetails {
		/**
		 * For parsing Json
		 */
		@SuppressWarnings("unused")
		private TestConnDetails() {
			this(-1);
		}
	}

	private final Logger logger = LoggerFactory.getLogger(ConnApiTest.class);
}
