package de.wuespace.telestion.api.verticle.trait;

import io.vertx.core.Vertx;

/**
 * Stores information to a specific timing from Vert.x like the timing id or associated Vert.x instance.
 * @param vertx the associated Vert.x instance
 * @param id the id of the timing
 *
 * @author Ludwig Richter
 */
public record Timing(Vertx vertx, long id) {
	/**
	 * Cancels the timing on the associated Vert.x instance.
	 * @return {@code true} if the timing was successfully cancelled
	 */
	public boolean cancel() {
		return vertx.cancelTimer(id);
	}
}
