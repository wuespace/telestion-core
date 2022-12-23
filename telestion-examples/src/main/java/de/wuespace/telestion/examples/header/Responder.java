package de.wuespace.telestion.examples.header;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;

import java.util.concurrent.atomic.AtomicInteger;

public class Responder extends TelestionVerticle<Responder.Configuration> implements WithEventBus {

	public static void main(String[] args) {
		Requester.main(args);
	}

	public record Configuration(@JsonProperty String respondAddress) implements TelestionConfiguration {
		public Configuration() {
			this("request-channel");
		}
	}

	@Override
	public void onStart() {
		var responseCounter = new AtomicInteger();

		register(verticleConfigStrategy.getConfig().respondAddress(), message -> {
			var requestInfos = DelayCounterInformation.from(message);
			var requestTimes = TimeInformation.from(message);

			logger.info("Request body: {}", message.body());
			logger.info("Request delay: {}", requestInfos.getDelay());
			logger.info("Request counter: {}", requestInfos.getCounter());
			logger.info("Request send time: {}", requestTimes.getSendTime());

			var sendTime = System.currentTimeMillis();
			var responseInfos = new DelayCounterInformation(
					DelayCounterInformation.DELAY_DEFAULT_VALUE,
					responseCounter.getAndIncrement());
			var responseTimes = new TimeInformation(requestTimes).setSendTime(sendTime);

			message.reply("Pong", HeaderInformation.merge(responseInfos, responseTimes).toOptions());
		});
	}
}
