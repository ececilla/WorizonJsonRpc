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
	 * 
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
	 */
	public <T> T call(String method, Map<String, Object> params, Class<T> clazz ) throws IOException, InterruptedException {
		
		return call(method, (Object)params, clazz);
	}
	
	/**
	 * Calls the remote procedure and serializes the result JSONRPC field into an object of the class clazz.
	 * @param method The remote procedure name to be invoked.
	 * @param params The params that will be passed into the remote procedure as a JSON array.
	 * @param clazz The class to turn into the result field.
	 */
	public <T> T call(String method, List<Object> params, Class<T> clazz ) throws IOException, InterruptedException{
		
		return call(method, (Object)params, clazz);
	}
	
	/**
	 * Calls the remote procedure and serializes the result JSONRPC field into an object of the class clazz.
	 * @param method The remote procedure name to be invoked.
	 * @param clazz The class to turn into the result field.
	 */
	public <T> T call(String method, Class<T> clazz ) throws IOException, InterruptedException{
		
		return call(method, (Object)null, clazz);
	}
	
	/**
	 * Adds a mapping between exception class and error code. When this code error arrives the mapped
	 * exception will be thrown.
	 * @param code The JSON-RPC code error.
	 * @param exception The exception to be thrown, this exception must extend RuntimeException.
	 */
	public void addRuntimeExceptionMapping( int code, Class<? extends RuntimeException> exception){
		
		exceptions.put(code, exception);
	}
			
}
