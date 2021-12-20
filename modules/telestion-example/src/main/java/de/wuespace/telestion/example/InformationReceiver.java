package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.example.information.SimpleInformation;
import de.wuespace.telestion.example.messages.SimpleMessage;

import java.util.Objects;

public class InformationReceiver extends TelestionVerticle<InformationReceiver.Configuration> implements WithEventBus {
	public record Configuration(@JsonProperty String inAddress) implements TelestionConfiguration {
	}

	@Override
	public void onStart() {
		register(getConfig().inAddress(), (body, message) -> {
			var info = SimpleInformation.fromHeaders(message.headers());
			if (Objects.isNull(info)) {
				logger.warn("Cannot extract simple information from header!");
			} else {
				logger.info("Received from Information sender: {}", info);
			}
		}, SimpleMessage.class);
		logger.info("InformationReceiver started");
	}
}
