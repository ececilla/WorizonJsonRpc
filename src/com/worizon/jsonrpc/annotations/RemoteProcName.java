package com.worizon.jsonrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation explicit changes the remote method name to be called.
 * <p>Ex:
 * <pre>
 * {@literal @}Remote
 * public interface MyInterface{
 * 
 *  {@literal @}RemoteProcName("my_sum")
 *  public int sum(int x, int y);
 * }
 * </pre>
 * <p>
 * The remote procedure will be called as "my_sum" despite of the method's name "sum".
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface RemoteProcName {
	
	String value();	
}
