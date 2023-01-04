package de.wuespace.telestion.services.logging;

import de.wuespace.telestion.api.verticle.NoConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;

/**
 * A verticle which logs the message traffic.
 *
 * @author Jan von Pichowski (@jvpichovski), Cedric Boes (@cb0s), Ludwig Richter (@fussel178)
 * @see io.vertx.core.eventbus.EventBus
 */
public final class MessageLogger extends TelestionVerticle<NoConfiguration> {

	@Override
	public void onStart() throws Exception {
		vertx.eventBus().addOutboundInterceptor(interceptor -> {
			try (var ignored = MessageMDC.putCloseable(null, interceptor.message())) {
				logger.info("Outbound message to {}: {}", interceptor.message().address(),
						interceptor.body().toString());
			}
			interceptor.next();
		});
		vertx.eventBus().addInboundInterceptor(interceptor -> {
			try (var ignored = MessageMDC.putCloseable(null, interceptor.message())) {
				logger.info("Inbound message to {}: {}", interceptor.message().address(),
						interceptor.body().toString());
			}
			interceptor.next();
		});
	}
}
