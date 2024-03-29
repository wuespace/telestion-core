package de.wuespace.telestion.api.verticle.trait;

import io.vertx.core.Handler;
import io.vertx.core.TimeoutStream;
import io.vertx.core.Verticle;

import java.time.Duration;

/**
 * Allows {@link Verticle} instances to get simplified access to the Vert.x timing functions
 * like {@link io.vertx.core.Vertx#setPeriodic(long, Handler) setPeriodic}
 * or {@link io.vertx.core.Vertx#setTimer(long, Handler) setTimer}.
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * public class MyVerticle extends TelestionVerticle<GenericConfiguration> implements WithTiming {
 *     @Override
 *     public void onStart() {
 *         var delay = Duration.ofSeconds(1);
 *         interval(delay, id -> logger.info("Ping!"));
 *     }
 * }
 * }
 * </pre>
 *
 * @author Ludwig Richter (@fussel178), Pablo Klaschka (@pklaschka)
 */
public interface WithTiming extends Verticle {

	/**
	 * Like {@link io.vertx.core.Vertx#setPeriodic(long, Handler) setPeriodic},
	 * but returns a special handler which cancels the interval when called.
	 * @return a handler which cancels the interval when called
	 */
	default Timing interval(long milliseconds, Handler<Long> handler) {
		var id = getVertx().setPeriodic(milliseconds, handler);
		return new Timing(getVertx(), id);
	}

	/**
	 * Like {@link #interval(long, Handler)}, but accepts a {@link Duration}.
	 */
	default Timing interval(Duration period, Handler<Long> handler) {
		return interval(period.toMillis(), handler);
	}

	/**
	 * @see io.vertx.core.Vertx#periodicStream(long)
	 */
	default TimeoutStream intervalStream(long milliseconds) {
		return getVertx().periodicStream(milliseconds);
	}

	/**
	 * Like {@link #intervalStream(long)}, but accepts a {@link Duration}.
	 */
	default TimeoutStream intervalStream(Duration period) {
		return intervalStream(period.toMillis());
	}

	/**
	 * Like {@link io.vertx.core.Vertx#setTimer(long, Handler) setTimer},
	 * but returns a special handler which cancels the timeout when called.
	 * @return a handler which cancels the timeout when called
	 */
	default Timing timeout(long milliseconds, Handler<Long> handler) {
		var id = getVertx().setTimer(milliseconds, handler);
		return new Timing(getVertx(), id);
	}

	/**
	 * Like {@link #timeout(long, Handler)}, but accepts a {@link Duration}.
	 */
	default Timing timeout(Duration delay, Handler<Long> handler) {
		return timeout(delay.toMillis(), handler);
	}

	/**
	 * @see io.vertx.core.Vertx#timerStream(long)
	 */
	default TimeoutStream timeoutStream(long milliseconds) {
		return getVertx().timerStream(milliseconds);
	}

	/**
	 * Like {@link #timeoutStream(long)}, but accepts a {@link Duration}.
	 */
	default TimeoutStream timeoutStream(Duration delay) {
		return timeoutStream(delay.toMillis());
	}
}
