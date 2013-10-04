package com.worizon.jsonrpc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.worizon.jsonrpc.annotations.Remote;
import com.worizon.jsonrpc.annotations.RemoteParams;
import com.worizon.jsonrpc.annotations.RemoteProcName;
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
	
	private synchronized <T> T call(String method, Object params, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, JsonRpcException{
		
		JsonRpcRequest req = new JsonRpcRequest(method, params);		
		String response = http.sendSyncPostRequest( req.toString() );//blocking call
		JsonRpcResponse<T> res =  new JsonRpcResponse<T>( response, clazz );		
		if(res.getError() != null)
			throw new JsonRpcException( res.getError() );
		else
			return res.getResult();	
	}
			
	public synchronized <T> T call(String method, Map<String, Object> params, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, JsonRpcException{
		
		return call(method, (Object)params, clazz);
	}
	
	public synchronized <T> T call(String method, List<Object> params, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, JsonRpcException{
		
		return call(method, (Object)params, clazz);
	}
			
}
