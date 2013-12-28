package com.worizon.jsonrpc;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.worizon.jsonrpc.annotations.LocalException;
import com.worizon.jsonrpc.annotations.LocalExceptions;
import com.worizon.jsonrpc.annotations.Remote;
import com.worizon.jsonrpc.annotations.RemoteParams;
import com.worizon.jsonrpc.annotations.RemoteProcName;
import com.worizon.net.HttpRequestBuilder;

/**
 * Rpc apis grouped into a namespace class. You can use the rpc service with 3 different apis:
 * <li>
 * <ul>Sync api</ul>
 * <ul>Async api</ul>
 * <ul>Proxy api</ul>
 * </li>
 */
public class Rpc {
	
	private Rpc(){}

	/**
	 * Synchronous interface to call remote procedures.
	 */	
	public static class Sync extends RpcImpl {
					
		public Sync( String endpoint ) throws MalformedURLException {
			
			super(endpoint);
		}
		
		public Sync( HttpRequestBuilder builder ){
			
			super(builder);
		}				
		
		/**
		 * Makes parent's call method public through the Sync api.
		 * @see com.worizon.jsonrpc.RpcImpl#call(java.lang.String, java.util.Map, java.lang.Class)
		 */
		@Override
		public <T> T call(String method, Class<T> clazz, Map<String, Object> params ) throws IOException, InterruptedException {
			
			return super.call(method, clazz, params );
		}
		
		/**
		 * Makes parent's call method public through the Sync api.
		 * @see com.worizon.jsonrpc.RpcImpl#call(java.lang.String, java.lang.Class)
		 */
		@Override
		public <T> T call(String method, Class<T> clazz ) throws IOException, InterruptedException{
			
			return super.call(method, clazz);
		}
		
		/**
		 * Makes parent's call method public through the Sync api. 
		 * @see com.worizon.jsonrpc.RpcImpl#call(java.lang.String, java.util.List, java.lang.Class)
		 */
		@Override
		public <T> T call(String method, Class<T> clazz, List<Object> params  ) throws IOException, InterruptedException{
			
			return super.call(method, clazz, params );
		}
		
		/**
		 *  Calls the remote procedure with varargs parameters.
		 *  @param method The remote procedure name.
		 *  @param clazz The return type class type.
		 *  @param params The remote parameters supplied to the remote procedure.
		 *  @return T An object of the remote procedure return type.
		 */
		public <T> T call(String method, Class<T> clazz, Object... params )throws IOException, InterruptedException{
			
			return super.call(method, clazz, transformParametersArrayIntoCollection(params) );
		}
			
		
		/**
		 * Calls the remote procedure with void as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 */
		public void callVoid( String method, Object... params ) throws IOException, InterruptedException{
						
			call(method, Void.class, transformParametersArrayIntoCollection(params) );
		}
			
		
		/**
		 * Calls the remote procedure with int as the result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as integer.
		 */
		public int callInteger( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, Integer.class, transformParametersArrayIntoCollection(params) );			
		}
			
		
		/**
		 * Calls the remote procedure with an array of ints as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of integers.
		 */
		public int[] callIntegerArray( String method, Object...params ) throws IOException, InterruptedException{
			
			Integer result[] = call(method, Integer[].class, transformParametersArrayIntoCollection(params) );			
			
			int resultPrimitive[] = new int[result.length];
			for(int i = 0; i < result.length; i++)
				resultPrimitive[i] = result[i].intValue();
			
			return resultPrimitive;
		}
				
		
		/**
		 * Calls the remote procedure with double as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as double.
		 */
		public double callDouble( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, Double.class, transformParametersArrayIntoCollection(params) );			
		}
		
		/**
		 * Calls the remote procedure with an array of doubles as a result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of doubles.
		 */
		public double[] callDoubleArray(String method, Object...params ) throws IOException, InterruptedException{
			
			Double result[] = call(method, Double[].class, transformParametersArrayIntoCollection(params) );			
			
			double resultPrimitive[] = new double[result.length];
			for(int i=0; i < result.length; i++)
				resultPrimitive[i] = result[i].doubleValue();
			
			return resultPrimitive;
		}	
		
		/**
		 * Calls the remote procedure with float as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as float.
		 */
		public float callFloat( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, Float.class, transformParametersArrayIntoCollection(params) );			
		}		
		
		/**
		 * Calls the remote procedure with an array of floats as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of floats.
		 */
		public float[] callFloatArray(String method, Object...params ) throws IOException, InterruptedException{
			
			Float result[] = call(method, Float[].class, transformParametersArrayIntoCollection(params) );			
			
			float resultPrimitive[] = new float[result.length];
			for(int i=0; i < result.length; i++)
				resultPrimitive[i] = result[i].floatValue();
			
			return resultPrimitive;
		}
				
		/**
		 * Calls the remote procedure with String as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as String.
		 */
		public String callString( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, String.class, transformParametersArrayIntoCollection(params) );			
		}
			
		
		/**
		 * Calls the remote procedure with an array of Strings as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of Strings.
		 */
		public String[] callStringArray(String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, String[].class, transformParametersArrayIntoCollection(params) );			
		}
				
		/**
		 * Calls the remote procedure with boolean as result and with no params.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as boolean.
		 */
		public boolean callBoolean( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, Boolean.class, transformParametersArrayIntoCollection(params) );			
		}
		
		/**
		 * Calls the remote procedure with boolean as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of booleans.
		 */
		public boolean[] callBooleanArray( String method, Object... params ) throws IOException, InterruptedException{
			
			Boolean[] result = call(method, Boolean[].class, transformParametersArrayIntoCollection(params) );			
			
			boolean resultPrimitive[] = new boolean[result.length];
			for(int i=0; i < result.length; i++)
				resultPrimitive[i] = result[i].booleanValue();
			
			return resultPrimitive;
		}
				
		/**
		 * Calls the remote procedure with short as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as short.
		 */
		public short callShort( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, Short.class, transformParametersArrayIntoCollection(params) );			
		}
				
		/**
		 * Calls the remote procedure with boolean as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of shorts.
		 */
		public short[] callShortArray( String method, Object... params ) throws IOException, InterruptedException{
			
			Short[] result = call(method, Short[].class, transformParametersArrayIntoCollection(params) );			
			
			short resultPrimitive[] = new short[result.length];
			for(int i=0; i < result.length; i++)
				resultPrimitive[i] = result[i].shortValue();
			
			return resultPrimitive;
		}
				
		/**
		 * Calls the remote procedure with long as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as long.
		 */
		public long callLong( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, Long.class, transformParametersArrayIntoCollection(params) );			
		}
			
		/**
		 * Calls the remote procedure with long as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of longs.
		 */
		public long[] callLongArray( String method, Object... params ) throws IOException, InterruptedException{
			
			Long[] result = call(method, Long[].class, transformParametersArrayIntoCollection(params) );			
			
			long resultPrimitive[] = new long[result.length];
			for(int i=0; i < result.length; i++)
				resultPrimitive[i] = result[i].longValue();
			
			return resultPrimitive;
		}
		
		
		/**
		 * Calls the remote procedure with char as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as char.
		 */
		public char callChar( String method, Object... params ) throws IOException, InterruptedException{
			
			return call(method, Character.class, transformParametersArrayIntoCollection(params) );			
		}
		
		
		/**
		 * Calls the remote procedure with char as result.
		 * @param method The remote procedure name to be invoked.
		 * @param params The arguments to get into the remote procedure serialized as an ordered list.
		 * @return The procedure return value as an array of chars.
		 */
		public char[] callCharArray( String method, Object... params ) throws IOException, InterruptedException{
			
			Character[] result = call(method, Character[].class, transformParametersArrayIntoCollection(params) );			
			
			char resultPrimitive[] = new char[result.length];
			for(int i=0; i < result.length; i++)
				resultPrimitive[i] = result[i].charValue();
			
			return resultPrimitive;
		}
				
	}
	
	/**
	 * Proxy interface to call remote procedures.
	 */
	public static class Proxy extends RpcImpl{
		
		public Proxy( String endpoint ) throws MalformedURLException{
			
			super(endpoint);
		}
		
		public Proxy( HttpRequestBuilder builder ){
			
			super(builder);
		}
		
		@SuppressWarnings("unchecked")
		public <T> T createProxy( Class<T> clazz ){
							
			if( !clazz.isAnnotationPresent(Remote.class) ){
				
				throw new IllegalArgumentException("This interface is not annotated as @Remote");
			}
			
			if( clazz.isAnnotationPresent(LocalExceptions.class) ){
				
				LocalException exceptionAnnotations[] = clazz.getAnnotation(LocalExceptions.class).value();
				for( LocalException exceptionAnnotation:exceptionAnnotations){
					exceptions.put(exceptionAnnotation.code(), exceptionAnnotation.exception());
				}
			}
			
			return (T)java.lang.reflect.Proxy.newProxyInstance(	
					clazz.getClassLoader(), 
					new Class[]{clazz},
					new InvocationHandler() {
						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							
							Object params = null;
							if( !method.isAnnotationPresent(RemoteParams.class)  ){
								
								params = (args != null)?Arrays.asList(args):null;							
							}else{
								
								String remoteParamNames[] = method.getAnnotation(RemoteParams.class).value();
								if(remoteParamNames.length != args.length)
									throw new IllegalArgumentException("Method params number does not match annotated remote params number");
								
								if( remoteParamNames.length == 1 && remoteParamNames[0].equals("params") && 
										(args[0] instanceof Map<?,?> || args[0] instanceof List<?> ) ){
										
									params = args[0];														
								}else{
									params = new LinkedHashMap<String, Object>();
									for(int i=0; i< args.length; i++){
										((Map<String, Object>)params).put(remoteParamNames[i], args[i]);
									}
								}
							}
																			
							RemoteProcName annotation = method.getAnnotation(RemoteProcName.class);
							String remoteProcName = (annotation != null)?annotation.value():method.getName();												
							return call( remoteProcName, method.getReturnType(), params );
						}
			});
			
		}			
	}
	
	/**
	 * Asynchronous interface to call remote procedures.
	 */
	public static class Async extends RpcImpl{
		
		public Async( String endpoint ) throws MalformedURLException{
			
			super(endpoint);
		}
		
		public Async( HttpRequestBuilder builder ){
			
			super(builder);
		}
		
		public void register( Object registree ){
			
		}
		
		public void unRegister( Object registree ){
			
		}
		
		public <T> void call(final String method, final Class<T> clazz, final Object... params){
			Thread t = new Thread(new Runnable(){
				
				@Override
				public void run(){
					try{
						T returnValue = call(method, clazz, transformParametersArrayIntoCollection(params) );
						//TODO: broadcast returnValue through the bus.
					}catch(InterruptedException ie){
						//TODO: broadcast interrupted exception through the bus.
					}catch(IOException ioe){
						//TODO: broadcast io exception through the bus.
					}catch(Exception ex){
						//TODO: broadcast other exceptions through the bus.
					}
				}
			});
			t.start();
		}
		
		public void callVoid( String method, Object... params ){
			
			call(method, Void.class, params);
		}
		
		public void callInteger( String method, Object... params ){
			
			call(method, Integer.class, params);
		}
		
		public void callIntegerArray( String method, Object... params ){
			
			call(method, Integer[].class, params);
		}
	}
	
	/**
	 * Return the parameters bundled in a apropiate container, List or HashMap depending on how these
	 * were passed in.
	 * @return Parameters container.
	 */
	private static Object transformParametersArrayIntoCollection( Object...params ){
		
		if(params == null)
			return null;
		
		if( TypesUtil.all(params).haveType(Map.Entry.class) ){
			
			Map<String,Object> mapParams = new LinkedHashMap<String, Object>();//preserve same order of named parameters as in the args array.
			for( Object arg: params){
				
				@SuppressWarnings("unchecked")
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>)arg;
				mapParams.put(entry.getKey(), entry.getValue());				
			}
			return mapParams;
		}else if( TypesUtil.any(params).haveType(Map.Entry.class) ){
			
			throw new IllegalArgumentException("Must pass ALL parameters as named parameters or NONE.");
		}else	
			return Arrays.asList(params);
	}
	
	/**
	 * Helper factory method to create a parameter (name, value) pair.
	 * @param paramName Name of this parameter.
	 * @param paramValue Value of this parameter.
	 * @return The pair of (paramName, paramValue) as an object of type AbstractMap.SimpleEntry
	 */
	public static Map.Entry<String, Object> RemoteParam(String paramName, Object  paramValue){
		
		return new AbstractMap.SimpleEntry<String, Object>(paramName,paramValue);
	}

}
