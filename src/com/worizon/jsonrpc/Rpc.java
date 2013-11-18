package com.worizon.jsonrpc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.worizon.jsonrpc.annotations.LocalException;
import com.worizon.jsonrpc.annotations.LocalExceptions;
import com.worizon.jsonrpc.annotations.Remote;
import com.worizon.jsonrpc.annotations.RemoteParams;
import com.worizon.jsonrpc.annotations.RemoteProcName;
import com.worizon.net.HttpRequester;

import static com.worizon.jsonrpc.Consts.*;

/**
 * 
 * Proxy utility to make remote procedure calls to JSON-RPC servers. To use the proxy method to make
 * rpc calls your interface must be annotated with @Remote. Some examples using this class.
 * 
 * Ex1:
 * 
 * <pre>
 * @Remote
 * interface MyRemoteInterface{
 *		
 *		public Void doTask();
 * };
 * 
 * RpcProxy proxy = new RpcProxy("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = proxy.create(MyRemoteInterface.class);
 * service.doTask(); ==> remote procedure
 * </pre>	
 * Ex2:
 * <pre>
 * @Remote
 * interface MyRemoteInterface{
 * 		
 * 		public int sum(int x, y);
 * }
 * 
 * RpcProxy proxy = new RpcProxy("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = proxy.create(MyRemoteInterface.class);
 * int result = service.sum(3,4); ==> parameters ordered by position {params:[3,4]}
 * </pre>
 * Ex3:
 * <pre>
 * @Remote
 * interface MyRemoteInterface{
 * 		
 * 		@RemoteParams({"x","y"})
 * 		public int  sum(int x, int y);
 * }
 * 
 * RpcProxy proxy = new RpcProxy("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = proxy.create(MyRemoteInterface.class);
 * int result = service.sum(3,4); ==> parameters ordered by name {params:{x:3,y:4}}
 * </pre>
 * Ex4:
 * 
 * @Remote
 * interface MyRemoteInterface{
 * 		
 * 		@RemoteProcName("my_sum")
 * 		public int sum(int x, int y);
 * }
 * 
 * RpcProxy proxy = new RpcProxy("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = proxy.create(MyRemoteInterface.class);
 * int result = service.sum(3,4); ==> parameters ordered by position {method:"my_sum",params:[3,4]}
 * </pre>
 * 
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 *
 */
public class Rpc{
	
	/**
	 * Requester to make HTTP requests.
	 */
	private  HttpRequester http;	
	
	/**
	 * Map that maps from int to Throwable.
	 */
	private Map<Integer, Class<? extends RuntimeException>> exceptions = new HashMap<Integer, Class<? extends RuntimeException>>();
	
	public Rpc( String endpoint ){
		
		this.http = new HttpRequester(endpoint);
	}
	
	public Rpc( HttpRequester http ){
		
		this.http = http;
	}
	
	/**
	 * Creates a proxied interface to call remote procedures.
	 * ex:
	 * <pre>
	 * @Remote
	 * public interface ICalculator{
	 * 		int sum(int x, int y);
	 * }
	 * ICalculator calculator = Rpc.createProxy(Icalculator.class);
	 * int result = calculator.sum(4,6);//blocking call
	 * </pre>
	 * @param clazz The interface to be proxied.
	 */
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
		
		return (T)Proxy.newProxyInstance(	
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
						return call( remoteProcName, params, method.getReturnType() );
					}
		});
		
	}
	
	/**
	 * Calls the remote procedure and serializes the result JSONRPC field into an object of the class clazz.
	 */
	private synchronized <T> T call(String method, Object params, Class<T> clazz ) throws IOException, InterruptedException {
		
		JsonRpcRequest req = new JsonRpcRequest(method, params);		
		String respStr = http.request( req.toString() );//blocking call
		JsonRpcResponse<T> res =  new JsonRpcResponse<T>( respStr, clazz );		
		if(res.getError() != null){
			if(res.getError().isCustomError()){
				Class<? extends RuntimeException> exceptionClass = exceptions.get(res.getError().getCode());
				if( exceptionClass != null ){
					try{					
						throw exceptionClass.getConstructor(String.class)
											.newInstance(res.getError().getMessage());							
					}catch(NoSuchMethodException nsm){
						throw new RemoteException( res.getError() );
					}catch(InvocationTargetException ite){
						throw new RemoteException( res.getError() );
					}catch(IllegalAccessException iae){
						throw new RemoteException( res.getError() );
					}catch(InstantiationException ie){
						throw new RemoteException( res.getError() );
					}
				}else
					throw new RemoteException( res.getError() );
			}else
				throw new JsonRpcException( res.getError() );			
		}else
			return res.getResult();	
	}
	
	/**
	 * Calls the remote procedure and serializes the result JSONRPC field into an object of the class clazz.
	 * @param method The remote procedure name to be invoked.
	 * @param params The params that will be passed into the remote procedure as a JSON object.
	 * @param clazz The class to turn itno the result field.
	 * @return The result field deserialized as a T object.
	 */
	public <T> T call(String method, Map<String, Object> params, Class<T> clazz ) throws IOException, InterruptedException {
		
		return call(method, (Object)params, clazz);
	}
	
	/**
	 * Calls the remote procedure and serializes the result JSONRPC field into an object of the class clazz.
	 * @param method The remote procedure name to be invoked.
	 * @param params The params that will be passed into the remote procedure as a JSON array.
	 * @param clazz The class to turn into the result field.
	 * @return The result field deserialized as a T object.
	 */
	public <T> T call(String method, List<Object> params, Class<T> clazz ) throws IOException, InterruptedException{
		
		return call(method, (Object)params, clazz);
	}
	
	/**
	 * Calls the remote procedure and serializes the result JSONRPC field into an object of the class clazz.
	 * @param method The remote procedure name to be invoked.
	 * @param clazz The class to turn into the result field.
	 * @return The result field deserialized as a T object.
	 */
	public <T> T call(String method, Class<T> clazz ) throws IOException, InterruptedException{
		
		return call(method, (Object)null, clazz);
	}
	
	/**
	 * Calls the remote procedure with void as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 */
	public void callVoid( String method ) throws IOException,InterruptedException {
		
		call(method, Void.class);
	}
	
	/**
	 * Calls the remote procedure with void as result.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 */
	public void callVoid( String method, Object... args) throws IOException, InterruptedException{
				
		call(method, Arrays.asList(args), Void.class);
	}
	
	/**
	 * Calls the remote procedure with int as result an with no params.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as integer.
	 */
	public int callInteger( String method ) throws IOException, InterruptedException{
		
		return call(method, Integer.class);
	}
	
	/**
	 * Calls the remote procedure with int as the result.
	 * @param method The remote procedure name to be invoked.
	 * @params args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as integer.
	 */
	public int callInteger( String method, Object... args) throws IOException, InterruptedException{
		
		return call(method, Arrays.asList(args), Integer.class);
	}
	
	/**
	 * Calls the remote procedure with an array of ints as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @return The precedure return value as an array of integers.
	 */
	public int[] callIntegerArray( String method ) throws IOException, InterruptedException{
		
		Integer result[] =  call(method, Integer[].class);
		int resultPrimitive[] = new int[result.length];
		for(int i = 0; i < result.length; i++)
			resultPrimitive[i] = result[i].intValue();
		
		return resultPrimitive;
	}
	
	/**
	 * Calls the remote procedure with an array of ints as result.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as an array of integers.
	 */
	public int[] callIntegerArray( String method, Object...args ) throws IOException, InterruptedException{
		
		Integer result[] =  call(method,Arrays.asList(args), Integer[].class);
		int resultPrimitive[] = new int[result.length];
		for(int i = 0; i < result.length; i++)
			resultPrimitive[i] = result[i].intValue();
		
		return resultPrimitive;
	}
	
	
	/**
	 * Calls the remote procedure with double as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as double.
	 */
	public double callDouble( String method ) throws IOException, InterruptedException{
		
		return call(method, Double.class);
	}
	
	/**
	 * Calls the remote procedure with double as result.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as double.
	 */
	public double callDouble( String method, Object... args ) throws IOException, InterruptedException{
		
		return call(method,Arrays.asList(args), Double.class);
	}
	
	/**
	 * Calls the remote procedure with an array of doubles as a result.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as an array of doubles.
	 */
	public double[] callDoubleArray(String method) throws IOException, InterruptedException{
		
		Double result[] = call(method, Double[].class);
		double resultPrimitive[] = new double[result.length];
		for(int i=0; i < result.length; i++)
			resultPrimitive[i] = result[i].doubleValue();
		
		return resultPrimitive;
	}

	/**
	 * Calls the remote procedure with an array of doubles as a result.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as an array of doubles.
	 */
	public double[] callDoubleArray(String method, Object...args) throws IOException, InterruptedException{
		
		Double result[] = call(method, Arrays.asList(args), Double[].class);
		double resultPrimitive[] = new double[result.length];
		for(int i=0; i < result.length; i++)
			resultPrimitive[i] = result[i].doubleValue();
		
		return resultPrimitive;
	}

	/**
	 * Calls the remote procedure with float as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as float.
	 */
	public float callFloat( String method ) throws IOException, InterruptedException{
		
		return call(method, Float.class);
	}
	
	/**
	 * Calls the remote procedure with float as result.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as float.
	 */
	public float callFloat( String method, Object... args ) throws IOException, InterruptedException{
		
		return call(method, Arrays.asList(args), Float.class);
	}
	
	/**
	 * Calls the remote procedure with an array of floats as result.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as an array of floats.
	 */
	public float[] callFloatArray(String method) throws IOException, InterruptedException{
		
		Float result[] = call(method, Float[].class);
		float resultPrimitive[] = new float[result.length];
		for(int i=0; i < result.length; i++)
			resultPrimitive[i] = result[i].floatValue();
		
		return resultPrimitive;
	}
	
	/**
	 * Calls the remote procedure with an array of floats as result.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as an array of floats.
	 */
	public float[] callFloatArray(String method, Object...args) throws IOException, InterruptedException{
		
		Float result[] = call(method, Arrays.asList(args), Float[].class);
		float resultPrimitive[] = new float[result.length];
		for(int i=0; i < result.length; i++)
			resultPrimitive[i] = result[i].floatValue();
		
		return resultPrimitive;
	}
	
	/**
	 * Calls the remote procedure with String as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as String.
	 */
	public String callString( String method ) throws IOException, InterruptedException{
		
		return call(method, String.class);
	}
	
	/**
	 * Calls the remote procedure with String as result.
	 * @param method The remote procedure name to be invoked.
	 * @param The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as String.
	 */
	public String callString( String method, Object... args ) throws IOException, InterruptedException{
		
		return call(method,Arrays.asList(args), String.class);
	}
	
	/**
	 * Calls the remote procedure with an array of Strings as result.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as an array of Strings.
	 */
	public String[] callStringArray(String method) throws IOException, InterruptedException{
		
		return call(method, String[].class);		
	}
	
	/**
	 * Calls the remote procedure with an array of Strings as result.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as an array of Strings.
	 */
	public String[] callStringArray(String method, Object... args) throws IOException, InterruptedException{
		
		return call(method, Arrays.asList(args), String[].class);		
	}
	
	/**
	 * Calls the remote procedure with boolean as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as boolean.
	 */
	public boolean callBoolean( String method ) throws IOException, InterruptedException{
		
		return call(method, Boolean.class);
	}
	
	/**
	 * Calls the remote procedure with boolean as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as boolean.
	 */
	public boolean callBoolean( String method, Object... args ) throws IOException, InterruptedException{
		
		return call(method,Arrays.asList(args), Boolean.class);
	}
	
	/**
	 * Calls the remote procedure with boolean as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @return The procedure return value as an array of booleans.
	 */
	public boolean[] callBooleanArray( String method ) throws IOException, InterruptedException{
		
		Boolean[] result =  call(method, Boolean[].class);
		boolean resultPrimitive[] = new boolean[result.length];
		for(int i=0; i < result.length; i++)
			resultPrimitive[i] = result[i].booleanValue();
		
		return resultPrimitive;
	}
	
	/**
	 * Calls the remote procedure with boolean as result and with no params.
	 * @param method The remote procedure name to be invoked.
	 * @param args The arguments to get into the remote procedure serialized as an ordered list.
	 * @return The procedure return value as an array of booleans.
	 */
	public boolean[] callBooleanArray( String method, Object... args ) throws IOException, InterruptedException{
		
		Boolean[] result =  call(method, Arrays.asList(args), Boolean[].class);
		boolean resultPrimitive[] = new boolean[result.length];
		for(int i=0; i < result.length; i++)
			resultPrimitive[i] = result[i].booleanValue();
		
		return resultPrimitive;
	}
	
	/**
	 * Adds a mapping between exception class and error code. When the code error <i>code</i> arrives the 
	 * exception <i>exception</i>will be thrown.
	 * @param code The JSON-RPC code error.
	 * @param exception The exception to be thrown, this exception must extend RuntimeException.
	 */
	public void addRuntimeExceptionMapping( int code, Class<? extends RuntimeException> exception){
		
		exceptions.put(code, exception);
	}		
			
}
