package org.telestion.application;

import de.jvpichowski.rocketsound.MockRocketPublisher;
import de.jvpichowski.rocketsound.messages.base.GpsData;
import de.jvpichowski.rocketsound.messages.base.Position;
import org.telestion.core.connection.EventbusTcpBridge;
import org.telestion.core.database.DataService;
import org.telestion.core.database.MongoDatabaseService;
import org.telestion.core.message.Address;
import org.telestion.core.monitoring.MessageLogger;
import org.telestion.launcher.Launcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RocketSound {

	public static void main(String[] args) {
		//For now use this approach please. I will add a deployment mechanism with a config later.
		//Have a look at the MockRocketPublisher implementation to see how we use configurations.
		var dataTypeMap = new HashMap<String, Class<?>>();
		dataTypeMap.put("gpsposition", GpsData.class);
		dataTypeMap.put("position", Position.class);

		Launcher.start(
				new MessageLogger(),
				new MockRocketPublisher(Address.incoming(MongoDatabaseService.class, "save")),
				new EventbusTcpBridge(
						"localhost", 9870,
						List.of(
								Address.incoming(MongoDatabaseService.class, "save"),
								Address.incoming(MongoDatabaseService.class, "find"),
								Address.incoming(DataService.class, "save"),
								Address.incoming(DataService.class, "find")
						),
						List.of(
								Address.outgoing(MockRocketPublisher.class, "pub")
						)),
				new MongoDatabaseService("raketenpraktikum", "raketenpraktikumPool"),
				new DataService(dataTypeMap, Collections.emptyMap())
		);
	}
}
