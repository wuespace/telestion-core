package de.jvpichowski.rocketsound;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.jvpichowski.rocketsound.messages.base.*;
import de.jvpichowski.rocketsound.messages.sound.Amplitude;
import de.jvpichowski.rocketsound.messages.sound.Spectrum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import java.time.Duration;
import org.telestion.core.config.Config;

public final class MockRocketPublisher extends AbstractVerticle {

	private final Configuration forcedConfig;
	private Configuration config;

	public MockRocketPublisher() {
		forcedConfig = null;
	}

	public MockRocketPublisher(String outgoing) {
		forcedConfig = new Configuration(outgoing);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		config = Config.get(forcedConfig, config(), Configuration.class);

		vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), h -> {
			vertx.eventBus().publish(config.address, new Amplitude(3.7).json());
			vertx.eventBus().publish(config.address, new Spectrum(2.7, 1004.3, new double[]{2.6, 0.0, 3.5, 100, 980.5}).json());
			vertx.eventBus().publish(config.address, new GpsData(3, 7, 4343345.0, -376.322, 42134894).json());
			vertx.eventBus().publish(config.address, new NineDofData(
					new Accelerometer(0.47, 3.5, 1.0),
					new Gyroscope(0.3, -7.2, -0.1),
					new Magnetometer(0.4, 28, -0.33)).json());
			vertx.eventBus().publish(config.address, new BaroData(
					new Pressure(67773.3),
					new Temperature(24.3),
					new Altitude(287.0)).json());
		});
		startPromise.complete();
	}

	private static record Configuration(@JsonProperty String address) {
		@SuppressWarnings("unused")
		private Configuration() {
			this("Outgoing");
		}
	}
}
