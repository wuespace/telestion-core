package de.wuespace.telestion.services.connection.rework.tcp;

import java.time.Duration;

public class TcpTimeouts {
	/**
	 * Close tcp connection after first received packet.
	 */
	public static final long NO_RESPONSES = -1;

	/**
	 * Never actively close active tcp connection.
	 */
	public static final long NO_TIMEOUT = 0;

	/**
	 * The default TCP connection timeout.
	 */
	public static final long DEFAULT_TIMEOUT = Duration.ofSeconds(30).toMillis();
}
