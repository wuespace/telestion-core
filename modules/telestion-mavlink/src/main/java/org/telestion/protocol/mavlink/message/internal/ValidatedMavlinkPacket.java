package org.telestion.protocol.mavlink.message.internal;

import org.telestion.protocol.mavlink.message.MavlinkMessage;
import org.telestion.protocol.mavlink.message.PacketInformation;

public record ValidatedMavlinkPacket(byte[] payload,
									 Class<? extends MavlinkMessage> clazz,
									 PacketInformation info) {
}
