package org.telestion.launcher;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Configurator extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        //Path currentRelativePath = Paths.get("");
        //String s = currentRelativePath.toAbsolutePath().toString();
        //System.out.println("Current relative path is: " + s);

        //ConfigStoreOptions fileStore = new ConfigStoreOptions()
        //        .setType("file")
        //        .setConfig(new JsonObject().put("path", "conf/config.json"));

        //ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(configRes -> {
            if(configRes.failed()){
                return;
            }
            System.out.println(configRes.result());
        });
        //retriever.listen();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {

    }
}
