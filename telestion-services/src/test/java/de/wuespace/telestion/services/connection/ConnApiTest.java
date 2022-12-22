package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.connection.rework.*;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

		// Because GitHub actions run... weird
		Thread.sleep(Duration.ofSeconds(1).toMillis());

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
							// Not the most beautiful implementation :(
							if (testDetails.receiverNumber() != Integer.parseInt(addr.replace("SenderIncoming", ""))) {
								continue;
							}
							logger.info("Received package to {}", testDetails.receiverNumber());
							assertThat(msg.rawData(), is(bytes));
							queue.add(testDetails.receiverNumber());
							if (queue.stream().distinct().allMatch(i -> i >= 0 && i <= SENDER_COUNT)) {
								var length = queue.toArray().length;
								var requestedLength = SENDER_COUNT*PACKAGE_COUNT;
								if (length > requestedLength) {
									testContext.failNow("Too many messages received");
								}
								if (length == requestedLength) {
									testContext.completeNow();
								}
							} else {
								testContext.failNow("Data from unknown sender received in test-case");
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

		// Because GitHub actions run... weird
		Thread.sleep(Duration.ofSeconds(1).toMillis());

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
	void testStaticSender(Vertx vertx, VertxTestContext testContext) throws Throwable {
		// Define some useful constants
		var PACKAGE_COUNT = 10;
		var bytes = new byte[] {'T', 'E', 'S', 'T'};
		var staticDetails = new TestConnDetails(42);

		// Create dummy-connection and test-evaluator
		var counter = new AtomicInteger(0);
		vertx.eventBus().consumer("StaticSenderPublish", raw -> {
			JsonMessage.on(ConnectionData.class, raw, msg -> {
				try {
					assertThat(msg.rawData(), is(bytes));
					assertThat(msg.conDetails(), is(staticDetails));
					logger.info("Received package no. {}", counter.incrementAndGet());
					if (counter.get() == PACKAGE_COUNT) {
						testContext.completeNow();
					} else if (counter.get() > PACKAGE_COUNT) {
						testContext.failNow("Too many packages received on the dummy test-sender");
					}
				} catch(Throwable e) {
					testContext.failNow(e);
					logger.error("An exception occurred while testing StaticSender", e);
				}
			});
		});

		// Create Static-Sender
		var staticSender = new StaticSender(new StaticSender.Configuration(
				"StaticSenderIn", "StaticSenderPublish", staticDetails));

		// Deployment of tested verticle mustn't fail!
		vertx.deployVerticle(staticSender, handler -> {
			if (handler.failed()) {
				testContext.failNow(handler.cause());
			}
		});

		// Because GitHub actions run... weird
		Thread.sleep(Duration.ofSeconds(1).toMillis());

		// Publish test-data
		logger.info("Sending packages to StaticSender with receiverNumber {}", staticDetails.receiverNumber());
		IntStream.range(0, PACKAGE_COUNT).forEach(i -> {
			logger.info("Sending package {} to StaticSender", i);
			vertx.eventBus().publish("StaticSenderIn", new RawMessage(bytes).json());
		});
		logger.info("All packages for StaticSender sent");

		// Wait for completion
		assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
	}

	@Test
	void testRealUse(Vertx vertx, VertxTestContext testContext) throws Throwable {
		// Not implemented, yet, so nothing should fail here
		testContext.completeNow();

		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
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
