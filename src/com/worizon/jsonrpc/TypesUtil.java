package com.worizon.jsonrpc;

/**
 * Utility class to check array object types.
 * @author Enric Cecilla
 * @since 1.0.0
 */
public final class TypesUtil {
		
	private TypesUtil(){}
	
	public interface TypeChecker{
		
		public boolean haveType(Class<?> clazz);
	}
	
	/**
	 * Checks whether all objects in this array are of the specified type or not.
	 * @param objs The array of objects to check.	
	 * @return True if all objects are from this Class, false otherwise. 
	 */
	public static TypeChecker all( final Object [] objs ){
		
		return new TypeChecker(){
			
			public boolean haveType(Class<?> clazz){
				
				for( Object obj : objs ){
					if( !clazz.isInstance(obj) )
						return false;
				}
				return true;				
			}
		};
	}
	
	/**
	 * Checks whether at least one object in this array is of the specified type or not.
	 * @param objs The array of objects to check.	 
	 * @return True if al least one object is an instance of this class, false otherwise.
	 */
	public static TypeChecker any( final Object [] objs ){
		
		return new TypeChecker(){
			
			public boolean haveType(Class<?> clazz){
				
				for( Object obj : objs ){			
					if( clazz.isInstance(obj) )
						return true;
				}
				return false;				
			}
		};
	}
	
	/**
	 * Checks whether none object in this array is of the specified type or not.
	 * @param objs The array of objects to check.	 
	 * @return True if none elements are an instance of this class, false otherwise.
	 */
	public static TypeChecker none( final Object [] objs ){
		
		return new TypeChecker(){
			
			public boolean haveType(Class<?> clazz){
				
				for( Object obj : objs ){
					if( clazz.isInstance(obj) )
						return false;
				}
				return true;				
			}
		};
	}
	

}
