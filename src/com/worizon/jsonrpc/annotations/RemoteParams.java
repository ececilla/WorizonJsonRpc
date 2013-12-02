package com.worizon.jsonrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation gives remote parameters a parameter name. It's mandatory
 * to annotate remote methods with this annotation when we are calling the remote procedure
 * with named parameters instead of numbered parameters.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME )
@Target(ElementType.METHOD)
public @interface RemoteParams {
		
	String[] value();
}
