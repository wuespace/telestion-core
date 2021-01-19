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
			vertx.eventBus().publish(config.address, new Amplitude(3.7f).json());
			vertx.eventBus().publish(config.address, new Spectrum(2.7f, 1004.3f, new float[]{2.6f, 0.0f, 3.5f, 100f, 980.5f}).json());
			vertx.eventBus().publish(config.address, new GpsData(3, 7, 434534.0f, -376.322f, 42134894).json());
			vertx.eventBus().publish(config.address, new NineDofData(
					new Accelerometer(0.47f, 3.5f, 1.0f),
					new Gyroscope(0.3f, -7.2f, -0.1f),
					new Magnetometer(0.4f, 28f, -0.33f)).json());
			vertx.eventBus().publish(config.address, new BaroData(
					new Pressure(67773.3f),
					new Temperature(24.3f),
					new Altitude(287.0f)).json());
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
