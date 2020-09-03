package org.telestion.adapter.mavlink;

import org.telestion.core.verticle.TcpAdapter;
import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {
    	new Heartbeat(0, 0, 0, 0, 0, 0);
        Launcher.start(
                new TcpAdapter(42024),
                new Receiver(),
                new MavlinkParser());
    }
    
}
