package com.worizon.jsonrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to expose an interface as Remote. All interfaces required to be called
 * as a remote service via the proxy api must be annotated as {@literal @}Remote.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME )
@Target(ElementType.TYPE)
public @interface Remote {

}
