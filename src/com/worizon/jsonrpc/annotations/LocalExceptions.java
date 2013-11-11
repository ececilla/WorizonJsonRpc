package com.worizon.jsonrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation container/array for LocalException annotations.
 * 
 *  @author Enric Cecilla
 *  @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME )
@Target(ElementType.TYPE)
public @interface LocalExceptions {

	LocalException[] value(); 
}
