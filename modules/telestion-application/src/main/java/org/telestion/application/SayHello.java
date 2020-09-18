package org.telestion.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.telestion.core.config.Config;

import java.time.Duration;
import java.util.UUID;

public final class SayHello extends AbstractVerticle {

    private static record Configuration(
            @JsonProperty long period,
            @JsonProperty String message) {
        private Configuration(){
            this(0, null);
        }
    }

    private final UUID uuid = UUID.randomUUID();
    private final Configuration forcedConfig;

    public SayHello(){
        this.forcedConfig = null;
    }

    public SayHello(long period, String message){
        this.forcedConfig = new Configuration(period, message);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var config = Config.get(forcedConfig, config(), Configuration.class);
        vertx.setPeriodic(Duration.ofSeconds(config.period()).toMillis(), timerId -> System.out.println(config.message()+" from "+uuid));
    }
}
