package org.telestion.core.connection.tcp;

import java.time.Duration;

public class TcpTimeouts {
	public static final Duration NO_RESPONSES = Duration.ofMillis(-1);		// Close tcp connection after first received packet
	public static final Duration NO_TIMEOUT = Duration.ofMillis(0);			// Never actively close active tcp connection
	public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);	// 30 secs.
}
