package org.telestion.protocol.mavlink;

import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.protocol.mavlink.annotation.MavInfo;
import org.telestion.protocol.mavlink.dummy.NetPacket;
import org.telestion.protocol.mavlink.message.Mavlink2Information;
import org.telestion.protocol.mavlink.message.RawMavlinkPacket;
import org.telestion.protocol.mavlink.message.internal.ValidatedMavlinkPacket;
import org.telestion.protocol.old_mavlink.security.X25Checksum;

import java.util.Arrays;

/**
 * @author Cedric Boes
 * @version 1.0
 */
public final class ValidatorMavlink2 extends Validator {

	/**
	 *
	 * @param config
	 */
	public ValidatorMavlink2(Config config) {
		super(config);
	}

	/**
	 *
	 * @param inAddress
	 * @param packetOutAddress
	 * @param parserInAddress
	 */
	public ValidatorMavlink2(String inAddress, String packetOutAddress, String parserInAddress) {
		super(inAddress, packetOutAddress, parserInAddress);
	}

	@Override
	public final void handleMessage(Message<?> msg) {
		JsonMessage.on(NetPacket.class, msg, packet -> {
			var raw = packet.raw();

			// Checking raw packet constraints and if the packet is a MAVLinkV2 packet
			if (!(raw != null && raw.length > 11 && raw[0] == (byte) 0xFD)) {
				return;
			}

			logger.debug("MavlinkV2-packet received");

			var length = raw[1];

			// It can be greater if e.g. the packet length is smaller than the raw stream input
			if (raw.length - 12 - length < 0) {
				logger.info("Broken MavlinkV2-packet received!");
				vertx.eventBus().publish(getPacketOutAddress(), new RawMavlinkPacket(raw, false));
				return;
			}

			var incompatFlags = raw[2];
			var compatFlags = raw[3];
			var seq = raw[4];
			var sysId = raw[5];
			var compId = raw[6];
			var msgId = raw[7] << 16 + raw[8] << 8 + raw[9];
			var payload = Arrays.copyOfRange(raw, 10, 10 + length);
			var checksum = Arrays.copyOfRange(raw, 10 + length, 10 + length + 2);

			// Currently supported incompatibility flags
			if (incompatFlags == 0x01) {
				if (raw.length >= 10 + length + 2 + 13) {
					logger.info("Broken MavlinkV2-packet received!");
					vertx.eventBus().publish(getPacketOutAddress(), new RawMavlinkPacket(raw, false));
					return;
				}

				var signature = Arrays.copyOfRange(raw, 10 + length + 2, 10 + length + 2 + 13);
				// TODO: Do the signature-check!
			}

			var clazz = MessageIndex.get(msgId);
			if (!clazz.isAnnotationPresent(MavInfo.class)) {
				logger.warn("Annotation missing for {} (MavlinkV2)!", clazz.getName());
				vertx.eventBus().publish(getPacketOutAddress(), new RawMavlinkPacket(raw, false));
				return;
			}
			var annotation = clazz.getAnnotation(MavInfo.class);

			if (X25Checksum.calculate(payload, annotation.crc()) != checksum[0] << 8 + checksum[1]) {
				logger.info("Checksum of received MavlinkV2-packet invalid!");
				vertx.eventBus().publish(getPacketOutAddress(), new RawMavlinkPacket(raw, false));
				return;
			}

			vertx.eventBus().publish(getPacketOutAddress(), new RawMavlinkPacket(raw, true));

			var mavInfo = new Mavlink2Information(incompatFlags, compatFlags, seq, sysId, compId);
			vertx.eventBus().publish(getParserInAddress(), new ValidatedMavlinkPacket(payload, clazz, mavInfo));
		});
	}

	/**
	 * Handles all logs for {@link ValidatorMavlink2 this} verticle.
	 */
	private final Logger logger = LoggerFactory.getLogger(ValidatorMavlink2.class);

}
