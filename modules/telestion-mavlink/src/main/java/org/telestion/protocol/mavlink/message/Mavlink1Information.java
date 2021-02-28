package org.telestion.protocol.mavlink.message.internal;

public record Mavlink1Information(int seq,
								  int sysId,
								  int compId) implements PacketInformation {
}
