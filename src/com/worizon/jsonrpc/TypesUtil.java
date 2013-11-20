package com.worizon.jsonrpc;

/**
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
public class TypesUtil {
	
	/**
	 * 
	 */
	public static boolean all( Object[] objs, Class<?> clazz ){
		
		for( Object obj : objs ){
			if( !clazz.isInstance(obj) )
				return false;
		}
		return true;
	}
	
	/**
	 * 
	 */
	public static boolean any( Object[] objs, Class<?> clazz ){
		
		for( Object obj : objs ){			
			if( clazz.isInstance(obj) )
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 */
	public static boolean none( Object[] objs, Class<?> clazz ){
		
		for( Object obj : objs ){
			if( clazz.isInstance(obj) )
				return false;
		}
		return true;
	}

}