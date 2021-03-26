package org.telestion.core.connection.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fazecast.jSerialComm.SerialPort;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.config.Config;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.connection.ConnectionData;
import org.telestion.core.connection.SenderData;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

/**
 * @author Cedric Boes, Jan v. Pichowski
 * @version 2.0
 */
public class SerialConn extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		config = Config.get(config, config(), Configuration.class);

		logger.info("Opening Connection on {}", config.serialPort());

		serialPort = SerialPort.getCommPort(config.serialPort());
		serialPort.openPort();

		// In
		vertx.setPeriodic(config.sampleRate().toMillis(), event -> {
			try {
				var inpStream = serialPort.getInputStream();
				var len = inpStream.available();
				if (len > 0) {
					logger.info("Reading available bytes");
					var data = inpStream.readNBytes(len);
					vertx.eventBus().publish(config.outAddress(),
							new ConnectionData(data, new SerialDetails(config.serialPort())).json());
				}
			} catch(IOException e) {
				logger.error("A critical error occurred while checking for new incoming data", e);
			}
		});

		// Out
		vertx.eventBus().consumer(config.inAddress(), raw -> {
			JsonMessage.on(SenderData.class, raw,
					msg -> Arrays.stream(msg.conDetails())
							.filter(det -> det instanceof SerialDetails)
							.map(det -> (SerialDetails) det)
							.forEach(det -> {
								if (det.serialPort().equals(config.serialPort())) {
									serialPort.writeBytes(msg.rawData(), msg.rawData().length);
								}
							})
			);
		});

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
								@JsonProperty Duration sampleRate) implements JsonMessage {
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null, null, null);
		}

		public Configuration(String inAddress, String outAddress, String serialPort) {
			this(inAddress, outAddress, serialPort, Duration.ofMillis(100));
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
