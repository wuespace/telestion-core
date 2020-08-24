package org.telestion.widget;

import org.telestion.core.verticle.HelloWorld;
import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {
        Launcher.start(WidgetBridge.class.getName());
    }
}
