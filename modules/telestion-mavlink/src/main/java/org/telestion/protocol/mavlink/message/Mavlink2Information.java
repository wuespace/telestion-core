package org.telestion.protocol.mavlink.message;

/**
 *
 * @param incompatFlags
 * @param compatFlags
 * @param seqNum
 * @param sysId
 * @param compId
 *
 * @author Cedric Boes
 * @version 1.0
 */
public record Mavlink2Information(int incompatFlags,
								  int compatFlags,
								  int seqNum,
								  int sysId,
								  int compId) implements PacketInformation {
}
