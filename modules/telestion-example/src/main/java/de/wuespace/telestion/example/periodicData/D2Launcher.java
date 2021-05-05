package de.wuespace.telestion.example.periodicData;

import de.wuespace.telestion.services.database.MongoDatabaseService;
import de.wuespace.telestion.services.database.PeriodicDataPublisher;
import de.wuespace.telestion.services.monitoring.MessageLogger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class D2Launcher extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(D2Launcher.class);

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(D2Launcher.class.getName());
		vertx.deployVerticle(MessageLogger.class.getName());
		// vertx.deployVerticle(DataService.class.getName());
		vertx.deployVerticle(new MongoDatabaseService(
				"daedalus2", "d2Pool"
		));
		vertx.deployVerticle(MockD2Publisher.class.getName());
		vertx.deployVerticle(MockD2DataDispatcher.class.getName());
		vertx.deployVerticle(new PeriodicDataPublisher("de.wuespace.telestion.example.periodicData.IMU", 1));
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		startPromise.complete();
	}
}
