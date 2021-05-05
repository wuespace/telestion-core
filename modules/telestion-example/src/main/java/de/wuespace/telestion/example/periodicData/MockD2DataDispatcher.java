package de.wuespace.telestion.example.periodicData;

import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.database.MongoDatabaseService;
import de.wuespace.telestion.services.message.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MockD2DataDispatcher extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(MockD2DataDispatcher.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.eventBus().consumer("D2DispatcherInc", msg -> {
			JsonMessage.on(System_t.class, msg, syst -> {
				var imu = new IMU(
						syst.imuAccX(),
						syst.imuAccY(),
						syst.imuAccZ(),
						syst.imuGyroX(),
						syst.imuGyroY(),
						syst.imuGyroZ()
				);
				vertx.eventBus().publish(Address.incoming(MongoDatabaseService.class, "save"), imu.json());
			});
		});
		startPromise.complete();
	}
}
