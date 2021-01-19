package de.jvpichowski.rocketsound;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageParser extends AbstractVerticle {

	public static String INCOMING_DATA = "UartConn";
	public static String OUTGOING_DATA = "Database";

	private String storedData = "";

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.eventBus().consumer(INCOMING_DATA, this::parseData);

		startPromise.complete();
	}

	private void parseData(Message<String> event){
		var data = event.body();
		List<String> allMatches = new ArrayList<>();
		vertx.sharedData().getLock(MessageParser.class.getName(), lockHandle -> {
			if(lockHandle.succeeded()){
				storedData += data;
				Matcher m = Pattern.compile("\\[.*?\\]").matcher(storedData);
				while (m.find()) {
					allMatches.add(m.group());
				}
				storedData = storedData.replaceAll("\\[.*?\\]", "");
				lockHandle.result().release();
			}
		});
		allMatches.forEach(this::parseMessage);
	}

	/**
	 * [...]
	 *
	 * @param message
	 */
	private void parseMessage(String message){
		//vertx.eventBus().publish(OUTGOING_DATA, new GPSData(5.7f, 4.3f, 6.7f));
	}
}
