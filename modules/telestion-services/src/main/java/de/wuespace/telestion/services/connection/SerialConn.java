package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.services.monitoring.MessageLogger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import com.fazecast.jSerialComm.*;

@Deprecated(since = "v0.1.3", forRemoval = true)
public final class SerialConn extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(SerialConn.class);

	private final Configuration forcedConfig;
	private SerialPort serialPort;

	public SerialConn(Configuration forcedConfig) {
		this.forcedConfig = forcedConfig;
	}

	public SerialConn(String serialPort, String consumeAddress, String publishAddress){
		this(new Configuration(serialPort, consumeAddress, publishAddress));
	}

	public SerialConn(){
		this(null);
	}

	@Override
	public void start(Promise<Void> startPromise) {
		LOG.info("Started SerialConn");
		var config = Config.get(forcedConfig, config(), Configuration.class);

		serialPort = SerialPort.getCommPort(config.serialPort);
		serialPort.openPort();
		vertx.setPeriodic(Duration.ofMillis(100).toMillis(), event -> {
			try {
				int len = serialPort.getInputStream().available();
				if(len > 0){
					var data = serialPort.getInputStream().readNBytes(len);
					vertx.eventBus().publish(config.publishAddress, new SerialData(data).json());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		vertx.eventBus().consumer(config.consumeAddress, event -> {
			JsonMessage.on(SerialData.class, event, data -> {
				serialPort.writeBytes(data.data(), data.data().length);
			});
		});
		startPromise.complete();
	}

	@Override
	public void stop() throws Exception {
		serialPort.closePort();
		super.stop();
	}

	private static record Configuration(@JsonProperty String serialPort, @JsonProperty String consumeAddress, @JsonProperty String publishAddress) {
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null, null);
		}
	}


	// ------------- For testing purpose -------------------//

	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		vertx.deployVerticle(new MessageLogger());
		vertx.deployVerticle(new SerialConn(new Configuration("COM8", "serial", "serial")));
	}
}
