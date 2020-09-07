package org.telestion.safer.mavlink;

import org.telestion.core.verticle.RandomPositionPublisher;
import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {
//      Launcher is not always working properly. The EventBus messages are not received
        // You have to start them with one launch call.
        // Otherwise the message do not get trough - jvpichowski
        Launcher.start(
                new MessageSafer(),
                new RandomPositionPublisher()
        );
        //Launcher.start(MessageSafer.class.getName());
    }
}