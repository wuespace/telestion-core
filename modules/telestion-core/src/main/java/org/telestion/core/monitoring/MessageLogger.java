package org.telestion.core.monitoring;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.logging.MessageMDC;

/**
 * A verticle which logs the message traffic.
 *
 * @author Jan von Pichowski, Cedric Boes
 * @version 1.0
 * @see AbstractVerticle
 */
public final class MessageLogger extends AbstractVerticle {

	/**
	 * {@link Logger Logger-Module} for the {@link MessageLogger} to use.
	 */
	private final Logger logger = LoggerFactory.getLogger(MessageLogger.class);

	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().addOutboundInterceptor(interceptor -> {
			try (var c = MessageMDC.putCloseable(null, interceptor.message())) {
				logger.info("Outbound message to {}: {}", interceptor.message().address(),
						interceptor.body().toString());
			}
			interceptor.next();
		});
		vertx.eventBus().addInboundInterceptor(interceptor -> {
			try (var c = MessageMDC.putCloseable(null, interceptor.message())) {
				logger.info("Inbound message to {}: {}", interceptor.message().address(),
						interceptor.body().toString());
			}
			interceptor.next();
		});
		startPromise.complete();
	}
}
