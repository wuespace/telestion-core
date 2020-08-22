package org.telestion.core.message;

import io.vertx.core.Verticle;

public final class Address {

    public static String from(Verticle source){
        return source.getClass().getName()+"/out";
    }

    public static String from(Verticle source, String method){
        return from(source)+"#"+method;
    }

    public static String to(Verticle target){
        return target.getClass().getName()+"/in";
    }

    public static String to(Verticle target, String method){
        return to(target)+"#"+method;
    }

}
