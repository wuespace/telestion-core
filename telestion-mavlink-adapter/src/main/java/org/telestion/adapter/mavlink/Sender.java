package org.telestion.adapter.mavlink;

import org.telestion.adapter.mavlink.message.MavlinkMessage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class Sender extends AbstractVerticle {
	
	public static final String address = "mavlink.sender";
	
	@Override
	public void start(Promise<Void> promise) {
		vertx.eventBus().consumer(address, msg -> {
			if(msg.body() instanceof MavlinkMessage) {
				
			}
		});
		promise.complete();
	}
}
