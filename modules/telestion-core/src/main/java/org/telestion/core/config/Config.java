package org.telestion.core.config;

import io.vertx.core.json.JsonObject;

public final class Config {

    public static <T> T get(T forcedConfig, JsonObject config, Class<T> type){
        return forcedConfig == null ? config.mapTo(type) : forcedConfig;
    }
}
