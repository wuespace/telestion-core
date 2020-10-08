package org.telestion.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.time.Duration;
import org.telestion.core.message.Address;

/**
 * A class which publishes positions every two seconds. A codec for {@link Position} has to be registered.
 */
public final class PositionPublisher extends AbstractVerticle {

	/**
	 * Internal. Don't use it! TODO remove it.<br>
	 * <br>
	 * A small self containing usage example.
	 *
	 * @param args console-arguments
	 */
	@Deprecated
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		// we have to register the codec for the used message! Do this in your Launcher.
		vertx.eventBus().consumer(Address.outgoing(PositionPublisher.class), msg -> {
			System.out.println("Received message: " + msg.body());
		});
		vertx.deployVerticle(PositionPublisher.class.getName());
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.setPeriodic(Duration.ofSeconds(2).toMillis(), timerId -> {
			vertx.eventBus().publish(Address.outgoing(this), new Position(0.3, 7.2, 8.0));
		});
		startPromise.complete();
	}
}
