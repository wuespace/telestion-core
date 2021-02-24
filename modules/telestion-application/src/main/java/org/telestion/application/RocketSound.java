package org.telestion.application;

import de.jvpichowski.rocketsound.MockRocketPublisher;
import org.telestion.core.config.ConfigurableApplication;
import org.telestion.core.monitoring.MessageLogger;
import org.telestion.launcher.Launcher;

public class RocketSound {

	public static void main(String[] args) {
		//For now use this approach please. I will add a deployment mechanism with a config later.
		//Have a look at the MockRocketPublisher implementation to see how we use configurations.
		var PublishAddr = "Publisher.Outgoing";
		Launcher.start(new ConfigurableApplication());//new MessageLogger(), new MockRocketPublisher(PublishAddr)//,
			//	new EventbusTcpBridge("localhost", 9870, Collections.emptyList(),
			//			Collections.singletonList(Address.outgoing(RandomPositionPublisher.class, "MockPos")))
		);
	}
}
