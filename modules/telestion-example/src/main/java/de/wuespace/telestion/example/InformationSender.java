package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.api.verticle.trait.WithSharedData;
import de.wuespace.telestion.example.information.SimpleInformation;
import de.wuespace.telestion.example.messages.SimpleMessage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

import java.time.Duration;

public class InformationSender extends TelestionVerticle<InformationSender.Configuration>
		implements WithEventBus, WithSharedData {
	public static void main(String[] args) {
		var vertx = Vertx.vertx();

		var senderConfig = new InformationSender.Configuration("simple-information");
		var receiverConfig = new InformationReceiver.Configuration("simple-information");

		vertx.deployVerticle(InformationSender.class, new DeploymentOptions().setConfig(senderConfig.json()));
		vertx.deployVerticle(InformationReceiver.class, new DeploymentOptions().setConfig(receiverConfig.json()));
	}

	public record Configuration(@JsonProperty String outAddress) implements TelestionConfiguration {
	}

	@Override
	public void onStart() {
		vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), this::sendMessage);
		logger.info("InformationSender started");
	}

	private void sendMessage(Long intervalId) {
		logger.debug("Try to send new message with information");
		LocalMap<String, Integer> map = localMap(MAP_KEY);
		int value1 = map.getOrDefault("value1", 0) + 1;
		int value2 = map.getOrDefault("value2", 0) + 1;

		map.put("value1", value1);
		map.put("value2", value2);
		logger.info("New values for InformationReceiver: {}:{}", value1, value2);

		var info = new SimpleInformation(value1, value2, 'a', null, "Hello World", 4, true);
		publish(getConfig().outAddress(), new SimpleMessage("Hello World", "This is a test message"), info);
		logger.debug("Sent message");
	}

	private static final String MAP_KEY = InformationSender.class.getName();
}
