package org.telestion.adapter.mavlink;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class Listener extends AbstractVerticle {
	@Override
	public void start(Promise<Void> promise) {
		promise.complete();
	}
}
