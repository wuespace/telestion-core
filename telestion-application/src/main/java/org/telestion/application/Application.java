package org.telestion.application;

import org.telestion.core.verticle.HelloWorld;
import org.telestion.core.verticle.MessageLogger;
import org.telestion.launcher.Launcher;

public class Application {

    public static void main(String[] args) {
        Launcher.start(
                MessageLogger.class.getName(),
                HelloWorld.class.getName()
        );
    }

}
