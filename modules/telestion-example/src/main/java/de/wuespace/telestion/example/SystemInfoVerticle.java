package de.wuespace.telestion.example;

import de.wuespace.telestion.api.verticle.TelestionVerticle;

import java.net.InetAddress;

/**
 * @author Pablo Klaschka, Ludwig Richter
 */
public class SystemInfoVerticle extends TelestionVerticle {
	@Override
	public void onStart() throws Exception {
		logger.info("System Hostname: {}", InetAddress.getLocalHost().getHostName());
		// [...]
	}
}
