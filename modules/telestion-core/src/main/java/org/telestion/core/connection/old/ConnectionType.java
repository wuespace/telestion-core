package org.telestion.core.connection.old;

public enum ConnectionType {
	TCP(TcpConn.Participant.class),
	UART(null);

	public final Class<? extends ConnectionDetails> senderClass;

	ConnectionType(Class<? extends ConnectionDetails> senderClass) {
		this.senderClass = senderClass;
	}
}
