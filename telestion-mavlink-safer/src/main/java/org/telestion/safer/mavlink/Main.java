package org.telestion.safer.mavlink;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.telestion.core.message.Address;
import org.telestion.core.message.JsonMessageCodec;
import org.telestion.core.message.Position;
import org.telestion.core.verticle.HelloWorld;
import org.telestion.core.verticle.PositionPublisher;
import org.telestion.launcher.Launcher;

public class Main {

    public static void main(String[] args) {

//        Launcher is not always working properly. The EventBus messages are not received
//        Launcher.start(SenderVerticle.class.getName());
//        Launcher.start(MessageSafer.class.getName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new SenderVerticle());
        vertx.deployVerticle(new MessageSafer("testAddress", "testBackup"));
    }
}