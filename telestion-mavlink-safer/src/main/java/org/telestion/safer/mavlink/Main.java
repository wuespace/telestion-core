package org.telestion.safer.mavlink;

import io.vertx.core.Vertx;

public class Main {

    public static void main(String[] args) {

//        Launcher is not always working properly. The EventBus messages are not received
//        Launcher.start(SenderVerticle.class.getName());
//        Launcher.start(MessageSafer.class.getName());

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new MessageSafer());
        vertx.deployVerticle(new RandomPositionPublisher());
    }
}