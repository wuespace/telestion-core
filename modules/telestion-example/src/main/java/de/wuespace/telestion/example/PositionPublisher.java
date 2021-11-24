package de.wuespace.telestion.example;

import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.example.messages.Position;
import io.vertx.core.Vertx;

import java.time.Duration;

import de.wuespace.telestion.services.message.Address;

/**
 * A class which publishes positions every two seconds. A codec for {@link Position} has to be registered.
 */
public final class PositionPublisher extends TelestionVerticle implements WithEventBus {

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
	public void onStart() throws Exception {
		vertx.setPeriodic(Duration.ofSeconds(2).toMillis(), timerId -> publish(
						Address.outgoing(this), new Position(0.3, 7.2, 8.0)
				)
		);
	}
}
