package org.telestion.core.connection;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.nio.Buffer;
import java.time.Duration;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.monitoring.MessageLogger;
import com.fazecast.jSerialComm.*;

public final class UartConn extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(UartConn.class);

	@Override
	public void start(Promise<Void> startPromise) {
		LOG.info("Started UartConn");

		//Uart init
		SerialPort serialPort = SerialPort.getCommPort("COM1");
		byte[] buffer = new byte[12];
		serialPort.openPort();
		vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), new Handler<Long>() {
			@Override
			public void handle(Long event) {

				//Uart auslesennnn

				serialPort.readBytes(buffer, 12);


				//Eventbus publishen
				vertx.eventBus().publish("UartData", Arrays.toString(buffer));
			}
		});
		startPromise.complete();
	}


	// ------------- For testing purpose -------------------//

	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		vertx.deployVerticle(new MessageLogger());
		vertx.deployVerticle(new UartConn());
	}
}
