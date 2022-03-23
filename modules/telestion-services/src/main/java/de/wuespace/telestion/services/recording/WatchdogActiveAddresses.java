package de.wuespace.telestion.services.recording;

import de.wuespace.telestion.api.verticle.TelestionVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class WatchdogActiveAddresses extends TelestionVerticle {
//	public static void main(String[] args) {
//		var vertx = Vertx.vertx();
//		vertx.deployVerticle(WatchdogActiveAddresses.class, new DeploymentOptions());
//	}

	@Override
	public void onStart() throws Exception {
		vertx.eventBus().addOutboundInterceptor(message -> {
			logger.info("recieved: " + message.body().toString() + " from " + message.message().address());
			message.next();
		});
	}
}
