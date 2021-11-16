package de.wuespace.telestion.services.connection.tcp;

import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.connection.ConnectionData;
import de.wuespace.telestion.services.connection.IpDetails;
import de.wuespace.telestion.services.connection.RawMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

import java.util.Map;

public abstract class BaseTcpVerticle extends AbstractVerticle {

	protected <T> void consumer(Message<T> raw, Map<IpDetails, ?> activeClients) {
		if (!JsonMessage.on(TcpData.class, raw, this::handleDispatchedMsg)) {
			if (!JsonMessage.on(ConnectionData.class, raw, this::handleMsg)) {
				// Broadcasting
				JsonMessage.on(RawMessage.class, raw,
						// Why not send directly? Well, we want logging and potential future updates
						msg -> activeClients.keySet().forEach(
								k -> this.handleDispatchedMsg(
										new TcpData(msg.data(),
												TcpDetails.fromIpDetails(k)))));
			}
		}
	}

	protected abstract void handleMsg(ConnectionData data);
	protected abstract void handleDispatchedMsg(TcpData tcpData);
}
