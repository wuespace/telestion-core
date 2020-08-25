package org.telestion.adapter.mavlink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.message.internal.RawMavlink;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV1;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV2;
import org.telestion.core.message.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class Transmitter extends AbstractVerticle {
	
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(Transmitter.class);
	
	/**
	 * 
	 */
	public static final String outAddress = Address.outgoing(Transmitter.class);
	
	/**
	 * 
	 */
	public static final String inAddress = Address.incoming(Transmitter.class);
	
	/**
	 * This ... simply didn't want to work so I had to write my own method...
	 * 
	 * @param <T>
	 * @param c
	 * @param ts
	 */
	private void addAll(Collection<Byte> c, byte[] ts) {
		for (byte b : ts) c.add(b);
	}
	
	@SuppressWarnings("preview")
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(inAddress, msg -> {
			if (msg.body() instanceof RawMavlinkV2 v2) {
				List<Byte> build = new ArrayList<Byte>(v2.len() + 11);
				byte[] raw1 = new byte[] {
						(byte) 0xFD,
						(byte) (v2.len() & 0xff),
						(byte) (v2.incompatFlags() & 0xff),
						(byte) (v2.compatFlags() & 0xff),
						(byte) (v2.seq() & 0xff),
						(byte) (v2.sysId() & 0xff),
						(byte) (v2.compId() & 0xff),
						(byte) ((v2.msgId() >> 16) & 0xff),
						(byte) ((v2.msgId() >> 8) & 0xff),
						(byte) (v2.msgId() & 0xff)
				};
				
				addAll(build, raw1);
				addAll(build, v2.payload().payload());
			} else if (msg.body() instanceof RawMavlinkV1 v1) {
				
			} else if (msg.body() instanceof RawMavlink raw) {
				logger.warn("Unsupported RawMavlink {} sent to {}", raw.getMavlinkId(), msg.address());
			} else {
				logger.error("Unsupported type sent to {}", msg.address());
			}
		});
		
		startPromise.complete();
	}
	
	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}
}
