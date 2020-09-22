package org.telestion.adapter.mavlink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.management.ReflectionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.exception.AnnotationMissingException;
import org.telestion.adapter.mavlink.exception.PacketException;
import org.telestion.adapter.mavlink.message.MavlinkMessage;
import org.telestion.adapter.mavlink.message.MessageIndex;
import org.telestion.adapter.mavlink.message.internal.RawMavlink;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV1;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV2;
import org.telestion.adapter.mavlink.security.X25Checksum;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.config.Config;
import org.telestion.core.connection.TcpConn;
import org.telestion.core.message.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;

/**
 * {@link Verticle} which handles outgoing MAVLink-Messages (as {@link RawMavlink}).</br>
 * Outgoing messages will be send to the {@link TcpServer}.
 *  
 * @author Cedric Boes, Jan von Pichowski
 * @version 1.0
 */
public final class Transmitter extends AbstractVerticle {

	/**
	 * The transmitter configuration.
	 *
	 * @param rawMavSupplierAddr {@link RawMavlink RawMavlink-messages} will be send here.
	 * @param tcpDataConsumerAddr  Messages (as {@link TcpData}) will be published on this address.
	 */
	@SuppressWarnings("preview")
	private static record Configuration(
			@JsonProperty String rawMavSupplierAddr,
			@JsonProperty String tcpDataConsumerAddr) {
		@SuppressWarnings("unused")
		private Configuration(){
			this(Address.incoming(Transmitter.class), Address.outgoing(Transmitter.class));
		}
	}

	/**
	 * All logs of {@link Transmitter} will be using this {@link Logger}.
	 */
	private final Logger logger = LoggerFactory.getLogger(Transmitter.class);

	/**
	 * This configuration will be used if not <code>null</code>.
	 */
	private final Configuration forcedConfig;

	/**
	 * Creates a transmitter with the default configuration or the file configuration if available.
	 */
	public Transmitter() {
		forcedConfig = null;
	}

	/**
	 * Creates a transmitter with the given addresses.
	 *
	 * @param rawMavSupplierAddr {@link RawMavlink RawMavlink-messages} will be send here.
	 * @param tcpDataConsumerAddr  Messages (as {@link TcpData}) will be published on this address.
	 */
	public Transmitter(String rawMavSupplierAddr, String tcpDataConsumerAddr) {
		forcedConfig = new Configuration(rawMavSupplierAddr, tcpDataConsumerAddr);
	}

	@Override
	public void start(Promise<Void> startPromise) {
		var config = Config.get(forcedConfig, config(), Configuration.class);

		vertx.eventBus().consumer(config.rawMavSupplierAddr(), msg -> {
			if (!(JsonMessage.on(RawMavlinkV1.class, msg, m -> interpretMsg(m, config.tcpDataConsumerAddr()))
					|| JsonMessage.on(RawMavlinkV2.class, msg, m -> interpretMsg(m, config.tcpDataConsumerAddr())))) {
				logger.error("Unsupported type sent to {}", msg.address());
			}
		});
		
		startPromise.complete();
	}
	
	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}


	/**
	 * Converts {@link RawMavlink RawMavlink-messages} to a {@link TcpData TcpData-objects}.
	 *
	 * @param mav {@link MavlinkMessage} to convert
	 */
	private void interpretMsg(RawMavlink mav, String outAddress) {
		// Creating Array for CRC-Calc
		byte[] raw = mav.getRaw();
		byte[] buildArray = Arrays.copyOfRange(raw, 1, raw.length -
				(mav instanceof RawMavlinkV2 && ((RawMavlinkV2) mav).incompatFlags() == 0x1 ? 12 : -1));

		// Apply Checksum
		Class<? extends MavlinkMessage> clazz = MessageIndex.get(mav instanceof RawMavlinkV2 ?
				((RawMavlinkV2) mav).msgId() :
				((RawMavlinkV1) mav).msgId());
		try {
			Method method = clazz.getMethod("getCrc", Void.class);
			buildArray[buildArray.length-1] = (byte) method.invoke(null, Void.class);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | AnnotationMissingException e) {
			logger.error("Getting CRC_EXTRA of {} failed! Cause:\n{}", clazz.getSimpleName(), e);
			throw new PacketException(new ReflectionException(e));
		}
		int crc = X25Checksum.calculate(buildArray);

		// Create new build Array
		buildArray = Arrays.copyOf(raw, raw.length + 2);

		buildArray[buildArray.length-2] = (byte) ((crc >> 8) & 0xff);
		buildArray[buildArray.length-1]	= (byte) (crc & 0xff);

		var addrPort = AddressAssociator.remove(mav.getMavlinkId());
		vertx.eventBus().send(outAddress, new TcpConn.Data(new TcpConn.Participant(addrPort.address(), addrPort.port()), buildArray));
	}
}
