package org.telestion.core.message;

import io.vertx.core.Verticle;

/**
 * Class for composing Addresses for the {@link Verticle Verticles}.</br>
 * There are:<ul>
 * <li><strong>outgoing:</strong> {@link Verticle Verticles} publish there results on the eventbus on this address if
 * the result is not for a specific {@link Verticle} alone</li>
 * <li><strong>incoming:</strong> {@link Verticle Verticles} listen on the eventbus for packages here</li></ul>
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
    public static String outgoing(Verticle source){
        return outgoing(source.getClass());
    }

	/**
	 * Creates a new outgoing address from a {@link Verticle} and returns it as a {@link String}.
	 *
	 * @param source {@link Verticle} to compose address from
	 * @return composed {@link String address}
	 */
	public static String outgoing(Class<? extends Verticle> source){
    	return source.getName()+"/out";
	}

    /**
     * Creates a new outgoing address from a method within a {@link Verticle} and returns it as a {@link String}.
     * 
     * @param source {@link Verticle} to compose address from
     * @param method Method which specifies the output address
     * @return composed {@link String address}
     */
    public static String outgoing(Verticle source, String method){
        return outgoing(source.getClass(), method);
    }

	/**
	 * Creates a new outgoing address from a method within a {@link Verticle} and returns it as a {@link String}.
	 *
	 * @param source {@link Verticle} to compose address from
	 * @param method Method which specifies the output address
	 * @return composed {@link String address}
	 */
	public static String outgoing(Class<? extends Verticle> source, String method){
		return outgoing(source)+"#"+method;
	}
    
	/**
	 * Creates a new incoming address from a {@link Verticle} and returns it as a {@link String}.
	 * 
	 * @param target {@link Verticle} to compose address from
	 * @return composed {@link String address}
	 */
    public static String incoming(Class<? extends Verticle> target){
        return target.getName()+"/in";
    }
    
    /**
     * Creates a new incoming address from a method within a {@link Verticle} and returns it as a {@link String}.
     * 
     * @param target {@link Verticle} to compose address from
     * @param method Method which specifies the input address
     * @return composed {@link String address}
     */
    public static String incoming(Class<? extends Verticle> target, String method){
        return incoming(target)+"#"+method;
    }
	/**
	 * Creates a new incoming address from a {@link Verticle} and returns it as a {@link String}.
	 *
	 * @param target {@link Verticle} to compose address from
	 * @return composed {@link String address}
	 */
	public static String incoming(Verticle target){
		return incoming(target.getClass());
	}

	/**
	 * Creates a new incoming address from a method within a {@link Verticle} and returns it as a {@link String}.
	 *
	 * @param target {@link Verticle} to compose address from
	 * @param method Method which specifies the input address
	 * @return composed {@link String address}
	 */
	public static String incoming(Verticle target, String method){
		return incoming(target.getClass(), method);
	}

}
