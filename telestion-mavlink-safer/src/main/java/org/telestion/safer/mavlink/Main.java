package org.telestion.safer.mavlink;

import org.telestion.core.verticle.HelloWorld;
import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {
        Launcher.start(HelloWorld.class.getName());
    }
}
