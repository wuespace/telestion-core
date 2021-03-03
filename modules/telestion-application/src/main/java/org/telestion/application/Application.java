package org.telestion.application;

import java.util.Collections;
import org.telestion.core.connection.EventbusTcpBridge;
import org.telestion.core.database.DataService;
import org.telestion.core.database.MongoDatabaseService;
import org.telestion.core.message.Address;
import org.telestion.core.monitoring.MessageLogger;
import org.telestion.core.web.WebServer;
//import org.telestion.example.RandomPositionPublisher;
//import org.telestion.launcher.Launcher;

/**
 * Starts the Telestion-Project as a standalone Application.
 *
 * @author Jan von Pichowski, Cedric Boes
 * @version 1.0
 */
public class Application {

	/**
	 * Calls the Launcher for a specific Testcase (at the moment).<br>
	 * Real functionality will be added later.
	 *
	 * @param args <em>unused at the moment</em>
	 */
	public static void main(String[] args) {
		/*Launcher.start(new MessageLogger(), new RandomPositionPublisher(),
				new EventbusTcpBridge("localhost", 9870,
						Collections.singletonList(Address.incoming(DataService.class, "find")),
						Collections.singletonList(Address.outgoing(RandomPositionPublisher.class, "MockPos"))),
				new WebServer(8080),
				new DataService(),
				new MongoDatabaseService("raketenpraktikum", "raketenpraktikumPool"));*/
	}

}
