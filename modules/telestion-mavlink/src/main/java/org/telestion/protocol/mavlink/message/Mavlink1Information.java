package org.telestion.protocol.mavlink.message;

public record Mavlink1Information(int seq,
								  int sysId,
								  int compId) implements PacketInformation {
}
