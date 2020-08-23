package org.telestion.core.message;

import io.vertx.core.Verticle;

/**
 * Class for composing Addresses for the {@link Verticle Verticles}.</br>
 * There are:<ul>
 * <li><strong>outgoing:</strong> {@link Verticle Verticles} publish there results on the eventbus on this address if
 * the result is not for a specific {@link Verticle} alone</li>
 * <li><strong>ingoing:</strong> {@link Verticle Verticles} listen on the eventbus for packages here</li></ul>
 * addresses.</br>
 * </br>
 * This class should only be used in static context.
 * 
 * @author Jan von Pichowsky, Cedric Boes
 * @version 1.0
 *
 */
public final class Address {

	/**
	 * Private Constructor! -> There shall be no objects!
	 */
	private Address() {}
	
	/**
	 * Creates a new outgoing address from a {@link Verticle} and returns it as a {@link String}.
	 * 
	 * @param source {@link Verticle} to compose address from
	 * @return composed {@link String address}
	 */
    public static String from(Verticle source){
        return source.getClass().getName()+"/out";
    }
    
    /**
     * Creates a new outgoing address from a method within a {@link Verticle} and returns it as a {@link String}.
     * 
     * @param source {@link Verticle} to compose address from
     * @param method Method which specifies the output address
     * @return composed {@link String address}
     */
    public static String from(Verticle source, String method){
        return from(source)+"#"+method;
    }
    
	/**
	 * Creates a new ingoing address from a {@link Verticle} and returns it as a {@link String}.
	 * 
	 * @param source {@link Verticle} to compose address from
	 * @return composed {@link String address}
	 */
    public static String to(Verticle target){
        return target.getClass().getName()+"/in";
    }
    
    /**
     * Creates a new ingoing address from a method within a {@link Verticle} and returns it as a {@link String}.
     * 
     * @param source {@link Verticle} to compose address from
     * @param method Method which specifies the input address
     * @return composed {@link String address}
     */
    public static String to(Verticle target, String method){
        return to(target)+"#"+method;
    }

}
