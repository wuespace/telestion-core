package org.telestion.widget;

import org.telestion.core.message.Address;
import org.telestion.core.verticle.RandomPositionPublisher;
import org.telestion.launcher.Launcher;

import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {
        Launcher.start(new WidgetBridge(
                "localhost",
                8080,
                Collections.emptyList(),
                Collections.singletonList(Address.outgoing(RandomPositionPublisher.class, "MockPos"))));
        Launcher.start(RandomPositionPublisher.class.getName());
    }
}
