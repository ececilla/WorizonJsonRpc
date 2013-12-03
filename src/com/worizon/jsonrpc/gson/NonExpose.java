package com.worizon.jsonrpc.gson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a field to be excluded of marshalling when the library converts the object
 * into a JSON String.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NonExpose {

}
