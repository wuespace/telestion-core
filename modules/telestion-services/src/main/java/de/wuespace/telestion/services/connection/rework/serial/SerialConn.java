package de.wuespace.telestion.services.connection.rework.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fazecast.jSerialComm.SerialPort;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.connection.rework.ConnectionData;
import de.wuespace.telestion.services.connection.rework.SenderData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Cedric Boes, Jan v. Pichowski
 * @version 2.0
 */
public final class SerialConn extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		config = Config.get(config, config(), Configuration.class);

		logger.info("Opening Connection on {}", config.serialPort());

		serialPort = SerialPort.getCommPort(config.serialPort());
		serialPort.openPort();

		// In
		vertx.setPeriodic(config.sampleTime(), event -> {
			try {
				var inpStream = serialPort.getInputStream();
				var len = inpStream.available();
				if (len > 0) {
					logger.debug("Reading available bytes");
					var data = inpStream.readNBytes(len);
					vertx.eventBus().publish(config.outAddress(),
							new ConnectionData(data, new SerialDetails(config.serialPort())).json());
				}
			} catch(IOException e) {
				logger.error("A critical error occurred while checking for new incoming data", e);
			}
		});

		// Out
		vertx.eventBus().consumer(config.inAddress(), raw -> JsonMessage.on(SenderData.class, raw,
				msg -> Arrays.stream(msg.conDetails())
						.filter(det -> det instanceof SerialDetails)
						.map(det -> (SerialDetails) det)
						.forEach(det -> {
							if (det.serialPort().equals(config.serialPort())) {
								logger.debug("Sending bytes");
								serialPort.writeBytes(msg.rawData(), msg.rawData().length);
							}
						})
		));

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		logger.info("Closing Connection on {}", config.serialPort());
		if (serialPort.isOpen()) {
			serialPort.closePort();
		} else {
			logger.warn("Connection on {} could not be closed because it was not open", config.serialPort());
		}
		stopPromise.complete();
	}

	public record Configuration(@JsonProperty String inAddress,
								@JsonProperty String outAddress,
								@JsonProperty String serialPort,
								@JsonProperty long sampleTime) implements JsonMessage {
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null, null, 0L);
		}

		public Configuration(String inAddress, String outAddress, String serialPort) {
			this(inAddress, outAddress, serialPort, 100);
		}
	}

	public SerialConn() {
		this(null);
	}

	public SerialConn(Configuration config) {
		this.config = config;
	}

	private Configuration config;
	private SerialPort serialPort;

	private static final Logger logger = LoggerFactory.getLogger(SerialConn.class);
}
