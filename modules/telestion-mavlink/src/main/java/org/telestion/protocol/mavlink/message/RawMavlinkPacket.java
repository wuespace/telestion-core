package org.telestion.protocol.mavlink.message;

public record RawMavlinkPacket(byte[] raw,
							boolean success) {
}
