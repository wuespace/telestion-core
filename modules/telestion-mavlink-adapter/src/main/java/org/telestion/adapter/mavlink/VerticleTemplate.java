package org.telestion.adapter.mavlink;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class VerticleTemplate extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startPromise) {
		startPromise.complete();
	}
	
	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}
}
