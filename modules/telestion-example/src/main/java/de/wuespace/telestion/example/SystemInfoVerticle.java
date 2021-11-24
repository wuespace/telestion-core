package de.wuespace.telestion.example;

import de.wuespace.telestion.api.verticle.TelestionVerticle;

import java.net.InetAddress;

public class SystemInfoVerticle extends TelestionVerticle {
	@Override
	public void onStart() throws Exception {
		logger.info("System Hostname: {}", InetAddress.getLocalHost().getHostName());
		// [...]
	}
}
