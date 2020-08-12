package org.telestion.application;

import org.telestion.core.verticle.HelloWorld;
import org.telestion.core.verticle.MessageLogger;
import org.telestion.launcher.Launcher;

/**
 * Starts the Telestion-Project as a standalone Application.
 * 
 * @version 1.0
 * @author Jan von Pichovsky, Cedric Boes
 *
 */
public class Application {
	
	/**
	 * Calls the Launcher for a specific Testcase (at the moment).</br>
	 * Real functionality will be added later.
	 * 
	 * @param args <i>unused at the moment</i>
	 */
    public static void main(String[] args) {
        Launcher.start(
                MessageLogger.class.getName(),
                HelloWorld.class.getName()
        );
    }

}
