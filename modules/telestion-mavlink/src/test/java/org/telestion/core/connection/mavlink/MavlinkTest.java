package org.telestion.core.connection.mavlink;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.telestion.adapter.mavlink.MavlinkParser;
import org.telestion.adapter.mavlink.Receiver;
import org.telestion.adapter.mavlink.Transmitter;
import org.telestion.adapter.mavlink.exception.PacketException;
import org.telestion.adapter.mavlink.message.MessageIndex;
import org.telestion.adapter.mavlink.message.RawPayload;
import org.telestion.adapter.mavlink.security.HeaderContext;
import org.telestion.adapter.mavlink.security.MavV2Signator;
import org.telestion.adapter.mavlink.security.SecretKeySafe;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.connection.TcpConn;
import org.telestion.core.monitoring.MessageLogger;
import org.telestion.mavlink.messages.mavlink.minimal.Heartbeat;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class MavlinkTest {
	
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(MavlinkTest.class);
	
    @Test
    void testReceiver(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var tcpToReceiver = "tcpToReceiver";
        var receiverToParser = "receiverToParser";
        var parserOut = "parserOut";
        var v1ToRaw = "v1ToRaw";
        var v2ToRaw = "v2ToRaw";
        var parserToTransmitter = "parserToTransmitter";

        if (!MessageIndex.isRegistered(HEARTBEAT_ID)) {
        	MessageIndex.put(HEARTBEAT_ID, Heartbeat.class);
        }
        
        vertx.deployVerticle(new TcpConn(null, 42124, tcpToReceiver, null, null));
        vertx.deployVerticle(new Receiver(tcpToReceiver, receiverToParser));
        vertx.deployVerticle(new MavlinkParser(new MavlinkParser.Configuration(
                        receiverToParser, parserOut,
                        v1ToRaw, v2ToRaw, parserToTransmitter)));

        vertx.eventBus().consumer(parserOut, msg -> {
            JsonMessage.on(Heartbeat.class, msg, heartbeat -> {
                testContext.completeNow();
            });
        });

        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start(Promise<Void> startPromise) throws Exception {
                var client = vertx.createNetClient();
                client.connect(42124, "localhost", netSocketResult -> {
                    assertThat(netSocketResult.succeeded(), is(true));
                    if(netSocketResult.failed()){
                        startPromise.fail(netSocketResult.cause());
                        return;
                    }
                    var socket = netSocketResult.result();
                    socket.write(Buffer.buffer(HEARTBEAT_MESSAGE_V2));
                    startPromise.complete();
                });
            }
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
    
    @Test
    void testTransmitter(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var receiverToParser = "receiverToParser";
        var parserOut = "parserOut";
        var v1ToRaw = "v1ToRaw";
        var v2ToRaw = "v2ToRaw";
        var parserToTransmitter = "parserToTransmitter";
        var transmitterConsumer = "transmitterConsumer";
    	
        if (!MessageIndex.isRegistered(HEARTBEAT_ID)) {
        	MessageIndex.put(HEARTBEAT_ID, Heartbeat.class);
        }
        
        byte[] secretKey = new byte[] {(byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0xA0};
        MavlinkParser parser = new MavlinkParser(
        		new HeaderContext((short) 0x0, (short) 0x0, (short) 0x1, (short) 0x1, (short) 0x2),
        		new SecretKeySafe(secretKey),
        		new MavlinkParser.Configuration(receiverToParser, parserOut, v1ToRaw, v2ToRaw, parserToTransmitter));
        
        vertx.deployVerticle(new Transmitter(parserToTransmitter, transmitterConsumer));
        vertx.deployVerticle(parser);
        vertx.deployVerticle(new MessageLogger());
        
        vertx.eventBus().consumer(transmitterConsumer, msg -> {
            // Test RawMavlinkV1
        	JsonMessage.on(RawPayload.class, msg, handler -> {
        		
        		try {
        			byte[] payload = handler.payload();
        			
        			var message = new byte[0];
        			if (payload[0] == (byte) 0xFE) {
            			message = HEARTBEAT_MESSAGE_V1;
            			message[2] = payload[2];		// Message-Index is unimportant
        			} else if (payload[0] == (byte) 0xFD) {
        				message = HEARTBEAT_MESSAGE_V2;
        				message[4] = payload[4];		// Message-Index is unimportant
        				
        				if (payload[2] == 0x1) {
        					System.out.println(Arrays.toString(payload));
        					System.out.println(Arrays.toString(message));
        					
        					message[2] = (byte) 0x1;
        					
        					System.out.println(Arrays.toString(message));
        					
        					var timestamp = Arrays.copyOfRange(payload, payload[1] + 11, payload[1] + 17);
        					var signature = MavV2Signator.rawSignature(secretKey, Arrays.copyOfRange(message, 1, 10),
				        							Arrays.copyOfRange(message, 10, 10 + message[1]), 0x32, (short) 0x2,
				        							timestamp);
        					var buildMsg = new byte[message.length + 13];
        					var index = 0;
        					for (byte b : message) {
        						buildMsg[index++] = b;
        					}
        					
        					buildMsg[index++] = (byte) 0x2;
        					
        					for (byte b : timestamp) {
        						buildMsg[index++] = b;
        					}
        					
        					for (byte b : signature) {
        						buildMsg[index++] = b;
        					}
        					
        					message = buildMsg;
        				}
        			} else {
        				testContext.failNow(new PacketException("First byte of payload has to be [0xFE, 0xFD]"));
        			}
        			
		    		assertThat(handler.payload(), is(message));
		    		testContext.completeNow();
        		} catch (AssertionError | NoSuchAlgorithmException e) {
        			testContext.failNow(e);
        		}
        	});
        });
        
        logger.info("Testing MAVLinkV1");
        vertx.eventBus().publish(v1ToRaw, new Heartbeat(1L, 2, 3, 4, 5, 6).json());
        assertThat(testContext.awaitCompletion(3, TimeUnit.SECONDS), is(true));
        
        logger.info("Testing MAVLinkV2 (without signing)");
        vertx.eventBus().publish(v2ToRaw, new Heartbeat(1L, 2, 3, 4, 5, 6).json());
        assertThat(testContext.awaitCompletion(3, TimeUnit.SECONDS), is(true));
        
        parser.changeHeaderContext(new HeaderContext((short) 0x01, (short) 0x0, (short) 0x0, (short) 0x0, (short) 0x2));
        logger.info("Testing MAVLinkV2 (with signing)");
        vertx.eventBus().publish(v2ToRaw, new Heartbeat(1L, 2, 3, 4, 5, 6).json());
        
        assertThat(testContext.awaitCompletion(3, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
        	throw testContext.causeOfFailure();
        }
    }
    
    private static final byte[] HEARTBEAT_MESSAGE_V1 = {
    		(byte) 0xFE,	// Mavlink V1
    		(byte) 0x09,	// Length
    		(byte) 0x00,	// Seq
    		(byte) 0x01,	// Comp-Id
    		(byte) 0x00,	// msg-id
    		(byte) 0x00,	// p
    		(byte) 0x00,	// a
    		(byte) 0x00,	// y
    		(byte) 0x01,	// l
    		(byte) 0x02,	// o
    		(byte) 0x03,	// a
    		(byte) 0x04,	// d
    		(byte) 0x05,	// V
    		(byte) 0x06,	// 1
    		(byte) 0x01,	// checksum #1
    		(byte) 0xC1,	// checksum #2
    };
    
    private static final byte[] HEARTBEAT_MESSAGE_V2 = {
            (byte)0xFD,		// Mavlink V2
            (byte)0x09,		// Length
            (byte)0x00,		// Incompat-Flags
            (byte)0x00,		// Compat-Flags
            (byte)0x00,		// Seq
            (byte)0x01,		// Sys-Id
            (byte)0x01,		// comp-id
            (byte)0x00,		// message-id #1
            (byte)0x00,		// message-id #2
            (byte)0x00,		// message-id #3
            (byte)0x09,		// p
            (byte)0x00,		// a
            (byte)0x02,		// y
            (byte)0x00,		// l
            (byte)0x00,		// o
            (byte)0x00,		// a
            (byte)0x00,		// d
            (byte)0x04,		// V
            (byte)0x14,		// 2
            (byte)0x54,		// checksum #1
            (byte)0x28		// checksum #2
    };
    
    private static final int HEARTBEAT_ID = 0;
}
