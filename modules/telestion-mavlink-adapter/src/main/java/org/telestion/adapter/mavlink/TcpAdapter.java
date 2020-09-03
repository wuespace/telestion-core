package org.telestion.adapter.mavlink;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.message.internal.MavConnection;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

/**
 * TODO: Java-Doc to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class TcpAdapter extends AbstractVerticle {
	
	/**
	 * 
	 */
	private NetServer server;
	
	/**
	 * 
	 */
	private final int port = 42024;
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(TcpAdapter.class);
	/*
	 * 
	 */
	private final HashMap<String, NetSocket> activeCons = new HashMap<String, NetSocket>();
	
	/**
	 * 
	 * @return
	 */
	private NetServerOptions configServer() {
		NetServerOptions options = new NetServerOptions();
		
		/*
		 * Modify Server as you wish
		 */
		options.setPort(port);
		
		return options;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Start the TCP-Server for MAVLink.</br>
	 * </br>
	 * {@inheritDoc}
	 */
	@Override
	public void start(Promise<Void> startPromise) {		
		logger.info("Starting TCP-Server for MAVLink");
		server = vertx.createNetServer(configServer());
		
		/*
		 * Handle Clients
		 */
		server.connectHandler(socket -> {
			String addr = socket.remoteAddress().toString();
			
			logger.info("Connection established ({})", addr);
			activeCons.put(addr, socket);
			
			socket.handler(buffer -> {
				vertx.eventBus().send(Receiver.inAddress,
						new MavConnection(buffer.getBytes(), addr).json());
			});
			
			socket.closeHandler(handler -> {
				activeCons.remove(addr);
				logger.info("Connection closed ({})", socket.remoteAddress());
			});
		});
		
		vertx.eventBus().consumer(Transmitter.outAddress, msg -> {
			
		});
		
		/*
		 * Handle Server Stuff 
		 */
		server.exceptionHandler(handler -> {
			logger.error("TCP-Server for MAVLink encountered an unexpected error:\n{}", handler);
		});
		
		server.listen(handler -> {
			if (handler.succeeded()) {
				logger.info("TCP-Server for MAVLink successfully started. Running on port {}", server.actualPort());
			} else {
				logger.error("Error while starting TCP-Server for MAVLink!", handler.cause());
				startPromise.fail("Starting TCP-Server for MAVLink failed!");
			}
		});
		
		startPromise.complete();
	}
	
	/**
	 * Stop the TCP-Server for MAVLink.</br>
	 * </br>
	 * {@inheritDoc}
	 */
	@Override
	public void stop(Promise<Void> stopPromise) {
		server.close(handler -> {
			if (handler.succeeded()) {
				logger.info("TCP-Server for MAVLink successfully stopped");
			} else {
				logger.error("Error while stopping TCP-Server for MAVLink! Cause:\n{}", handler.cause());
			}
		});
		
		stopPromise.complete();
	}
}
