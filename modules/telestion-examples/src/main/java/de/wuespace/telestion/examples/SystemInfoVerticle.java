package de.wuespace.telestion.examples;

import de.wuespace.telestion.api.verticle.NoConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;

import java.net.InetAddress;

/**
 * @author Pablo Klaschka (@pklaschka), Ludwig Richter (@fussel178)
 */
public class SystemInfoVerticle extends TelestionVerticle<NoConfiguration> {
	@Override
	public void onStart() throws Exception {
		logger.info("System Hostname: {}", InetAddress.getLocalHost().getHostName());
		// [...]
	}
}
