package de.wuespace.telestion.examples.header;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;

public class Receiver extends TelestionVerticle<Receiver.Configuration> implements WithEventBus {

	public static void main(String[] args) {
		Publisher.main(args);
	}

	public record Configuration(@JsonProperty String inAddress) implements TelestionConfiguration {
		public Configuration() {
			this("publish-channel");
		}
	}

	@Override
	public void onStart() {
		register(getConfig().inAddress(), message -> {
			var infos = HeaderInformation.from(message);
			var delay = infos.getInt("delay", -1);
			var counter = infos.getInt("counter", -1);

			logger.info("Received message: {}", message.body());
			logger.info("Publisher delay: {}", delay);
			logger.info("Publisher counter: {}", counter);
		});
	}
}
