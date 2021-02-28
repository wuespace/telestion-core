package org.telestion.application;

import de.jvpichowski.rocketsound.MockRocketPublisher;
import java.util.Collections;
import java.util.List;
import org.telestion.core.connection.EventbusTcpBridge;
import org.telestion.core.database.DataListener;
import org.telestion.core.database.DataService;
import org.telestion.core.database.MongoDatabaseService;
import org.telestion.core.message.Address;
import org.telestion.core.monitoring.MessageLogger;
import org.telestion.launcher.Launcher;

public class RocketSound {

	public static void main(String[] args) {
		//For now use this approach please. I will add a deployment mechanism with a config later.
		//Have a look at the MockRocketPublisher implementation to see how we use configurations.

		Launcher.start(
				new MessageLogger(),
				new MockRocketPublisher(Address.outgoing(MockRocketPublisher.class, "pub")),
				new EventbusTcpBridge(
						"localhost", 9870,
						List.of(
								Address.incoming(MongoDatabaseService.class, "save"),
								Address.incoming(MongoDatabaseService.class, "find"),
								Address.incoming(DataService.class, "save"),
								Address.incoming(DataService.class, "find")
						),
						List.of(
								Address.outgoing(MockRocketPublisher.class, "pub"),
								Address.outgoing(MongoDatabaseService.class, "save")
						)),
				new MongoDatabaseService("raketenpraktikum", "raketenpraktikumPool"),
				new DataService(Collections.emptyMap()),
				new DataListener(
						List.of(
								Address.outgoing(MockRocketPublisher.class, "pub")
						)
				)
		);
	}
}
