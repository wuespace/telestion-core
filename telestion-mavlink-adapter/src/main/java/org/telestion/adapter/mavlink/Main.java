package org.telestion.adapter.mavlink;

import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {
        Launcher.start(TcpAdapter.class.getName(), Receiver.class.getName(), MavlinkParser.class.getName(),
        		AddressAssociator.class.getName());
    }
    
}
