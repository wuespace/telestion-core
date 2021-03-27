package org.telestion.core.connection.tcp;

import java.time.Duration;

public class TcpTimeouts {
	public static final long NO_RESPONSES = -1;		// Close tcp connection after first received packet
	public static final long NO_TIMEOUT = 0;		// Never actively close active tcp connection
	public static final long DEFAULT_TIMEOUT = Duration.ofSeconds(30).toMillis();	// 30 secs.
}
