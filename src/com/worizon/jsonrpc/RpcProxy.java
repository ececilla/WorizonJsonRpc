package com.worizon.jsonrpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * @author Enric Cecilla
 * @since 1.0.0
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
	
	private synchronized <T> T call(String method, Object params, Class<T> clazz ) throws Exception{
		
		JsonRpcRequest req = new JsonRpcRequest(method, params);		
		String respStr = http.request( req.toString() );//blocking call
		JsonRpcResponse<T> res =  new JsonRpcResponse<T>( respStr, clazz );		
		if(res.getError() != null){
			if(res.getError().isCustomError())
				throw new RemoteException( res.getError() );
			else
				throw new JsonRpcException( res.getError() );			
		}else
			return res.getResult();	
	}
			
	public synchronized <T> T call(String method, Map<String, Object> params, Class<T> clazz ) throws Exception{
		
		return call(method, (Object)params, clazz);
	}
	
	public synchronized <T> T call(String method, List<Object> params, Class<T> clazz ) throws Exception{
		
		return call(method, (Object)params, clazz);
	}
			
}
