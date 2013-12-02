package com.worizon.jsonrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.worizon.jsonrpc.JsonRpcException;

/**
 * This annotation maps a JSON-RPC error code with a local Exception class. When the error code arrives
 * from the server, the library will throw the mapped exception instead of throwing the default
 * {@link JsonRpcException}.
 * 
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME )
@Target(ElementType.TYPE)
public @interface LocalException {

	int code();
	Class<? extends RuntimeException> exception(); 
}
