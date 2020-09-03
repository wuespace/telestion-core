package org.telestion.adapter.mavlink;

import org.telestion.adapter.mavlink.message.Heartbeat;
import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {
    	new Heartbeat(0, 0, 0, 0, 0, 0);
        Launcher.start(TcpAdapter.class.getName(), Receiver.class.getName(), MavlinkParser.class.getName());
    }
    
}
