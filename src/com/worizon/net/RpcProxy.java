package com.worizon.net;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.worizon.jsonrpc.JsonRpcException;
import com.worizon.jsonrpc.JsonRpcRequest;
import com.worizon.jsonrpc.JsonRpcResponse;
import com.worizon.jsonrpc.Remote;
import com.worizon.jsonrpc.RemoteParams;
import com.worizon.jsonrpc.RemoteProcName;
import com.worizon.net.HttpRequester;

/**
 * 
 * Interfaz de servicio para llamadas remotas sobre HTTP. En el contexto de este proyecto 
 * las llamadas remotas se haran sobre el protocolo HTTP, pasando dichas invocaciones como
 * mensajes XML, Json u otro mensaje en forma de texto..
 * 
 * 
 * @author enric
 *
 */
public class RpcProxy{
	
	private  HttpRequester http;			
	
	public RpcProxy( String endpoint ){
		
		this.http = new HttpRequester(endpoint);
	}
	
	public RpcProxy( HttpRequester http ){
		
		this.http = http;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T create( Class<T> clazz ){
						
		if( !clazz.isAnnotationPresent(Remote.class) ){
			
			throw new IllegalArgumentException("This interface is not annotated as @Remote");
		}
		
		return (T)Proxy.newProxyInstance(	
				clazz.getClassLoader(), 
				new Class[]{clazz},
				new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						
						if( !method.isAnnotationPresent(RemoteParams.class) )
							throw new IllegalArgumentException("This method is not annotated with remote params");
						
						String remoteParamNames[] = method.getAnnotation(RemoteParams.class).value();
						if( remoteParamNames.length != args.length )
							throw new IllegalArgumentException("Number of remote params and method params mismatch");
						
						Map<String, Object > params = null;
						if( remoteParamNames.length == 1 && remoteParamNames[0].equals("params") && args[0] instanceof Map<?,?> ){
							params = (Map<String, Object>)args[0];
						}else{
							params = new LinkedHashMap<String, Object>();																			
							for(int i=0; i< args.length; i++){
								params.put(remoteParamNames[i], args[i]);
							}
						}
													
						RemoteProcName annotation = method.getAnnotation(RemoteProcName.class);
						String remoteProcName = (annotation != null)?annotation.value():method.getName();
						return call( remoteProcName, params, method.getReturnType() );
					}
		});
		
	}
	
	public synchronized <T> T call( Map<String, Object> params, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, JsonRpcException{
		
		return call(null, params, clazz);		
	}
	
	public synchronized <T> T call(String method, Map<String, Object> params, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, JsonRpcException{
		
		JsonRpcRequest req = (method == null)?new JsonRpcRequest(3,params):new JsonRpcRequest(method, params);		
		String response = http.sendSyncPostRequest( req.toString() );//blocking call
		JsonRpcResponse<T> res =  new JsonRpcResponse<T>( response, clazz );		
		if(res.getError() != null)
			throw new JsonRpcException( res.getError() );
		else
			return res.getResult();	
	}
			
}
