package org.telestion.widget;

import org.telestion.core.verticle.RandomPositionPublisher;
import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {
        Launcher.start(WidgetBridge.class.getName());
        Launcher.start(RandomPositionPublisher.class.getName());
    }
}
