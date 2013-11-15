package com.worizon.jsonrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
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
