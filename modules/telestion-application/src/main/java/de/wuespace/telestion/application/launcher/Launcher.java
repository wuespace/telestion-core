package de.wuespace.telestion.application.launcher;

import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

/**
 * A {@link Launcher} is the first part of the launch process in Telestion.
 * <p>
 * It starts other processes like {@link de.wuespace.telestion.application.loader.Loader Loaders}
 * or {@link io.vertx.core.Verticle verticles}, configures {@link io.vertx.core.Vertx} instances or runs external code.
 * <p>
 * A launcher has a {@link #start() start} and {@link #stop() stop} method to start and stop it.
 *
 * @author Ludwig Richter (@fussel178)
 */
public interface Launcher {

	static Logger logger = LoggerFactory.getLogger(Launcher.class);

	/**
	 * <h4>Description</h4>
	 * Starts the {@link Launcher} and registers a {@link Runtime#addShutdownHook(Thread) shutdownHook}
	 * on the JVM runtime to gracefully stop the launcher if the JVM is gracefully stopped.
	 *
	 * <h4>Usage</h4>
	 * Use this method in the main method of your launcher:
	 * <pre>
	 * {@code
	 * public class MyLauncher implements Launcher {
	 *     public static void main(String[] args) {
	 *         Launcher.main(new MyLauncher());
	 *     }
	 *
	 *     // [...]
	 * }
	 * }
	 * </pre>
	 *
	 * @param launcher the Launcher that starts and stops
	 */
	static void main(Launcher launcher) throws Exception {
		// define thread for shutdown hook on JVM runtime
		Thread stopHook = new Thread(() -> {
			try {
				var future = launcher.stop()
						.onSuccess(stopResult -> logger.info("TelestionLauncher successfully stopped"))
						.onFailure(cause -> logger.error("TelestionLauncher stopped with errors:", cause))
						.onFailure(cause -> System.exit(1));

				while (!future.isComplete()) {
					//noinspection BusyWait
					sleep(200);
				}
			} catch (Exception e) {
				logger.error("TelestionLauncher stopped with errors:", e);
				System.exit(1);
			}
		});

		launcher.start()
				.onSuccess(startResult -> logger.info("TelestionLauncher successfully started"))
				.onFailure(cause -> logger.error("TelestionLauncher started with errors:", cause))
				// register shutdown hook for graceful shutdown
				.onComplete(result -> Runtime.getRuntime().addShutdownHook(stopHook));
	}

	/**
	 * Starts the launcher and all registered processes.
	 *
	 * @return a future which resolves when the startup successfully completes
	 */
	Future<Void> start() throws Exception;

	/**
	 * Stops the launcher and all registered processes.
	 *
	 * @return a future which resolves when the shutdown successfully completes
	 */
	Future<Void> stop() throws Exception;
}
