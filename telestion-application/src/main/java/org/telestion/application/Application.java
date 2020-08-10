package org.telestion.application;

import org.telestion.core.HelloWorld;
import org.telestion.launcher.Launcher;

public class Application {

    public static void main(String[] args) {
        Launcher.start(HelloWorld.class.getName());
    }

}
