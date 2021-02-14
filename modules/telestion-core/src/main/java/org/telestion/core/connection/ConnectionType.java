package org.telestion.core.connection;

public enum ConnectionType {
	TCP(TcpConn.Participant.class),
	UART(null);

	public final Class<? extends ConnectionDetails> senderClass;

	ConnectionType(Class<? extends ConnectionDetails> senderClass) {
		this.senderClass = senderClass;
	}
}
