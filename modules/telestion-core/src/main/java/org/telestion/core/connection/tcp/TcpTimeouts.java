package org.telestion.core.connection.tcp;

public class TcpTimeouts {
	public static final int NO_RESPONSES = -1;			// Close tcp connection after first received packet
	public static final int NO_TIMEOUT = 0;				// Never actively close active tcp connection
	public static final int DEFAULT_TIMEOUT = 30_000;	// 30 secs.
}
