package com.worizon.jsonrpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.worizon.net.HttpRequest;
import com.worizon.net.HttpRequestBuilder;


/**
 * 
 * Facade class to make remote procedure calls to JSON-RPC 2.0 compliant servers. This facade class can be consumed via the proxy api and 
 *  via the direct api (the set of methods named as callX). To use the proxy method to make rpc calls your interface must be annotated 
 * with the {@literal @}Remote annotation. There exists some other annotations apart from {@literal @}Remote  to tweak and adjust
 * the remote procedure behaviour to your needs. Following you can find the list of annotations that can be used to modify the behaviour of
 * the resulting rpc stub object: 
 * <ul>
 * <li>{@literal @}RemoteProcName</li>
 * <li>{@literal @}RemoteParams</li>
 * <li>{@literal @}LocalExceptions</li>
 * <li>{@literal @}LocalException</li>
 * </ul>
 * <p> 
 * <strong>Some examples using the proxy api:</strong>
 * <p>
 * Ex1:
 * 
 * <pre>
 * <strong>{@literal @}Remote</strong> (Notice the annotation)
 * interface MyRemoteInterface{
 *		
 *	public Void doTask();
 * };
 * 
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = rpc.createProxy(MyRemoteInterface.class);
 * service.doTask(); //==> blocking call to the remote procedure
 * </pre>	
 * <p>
 * Ex2:
 * <pre>
 * {@literal @}Remote
 * interface MyRemoteInterface{
 * 		
 * 	public int sum(int x, y);
 * }
 * 
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = rpc.createProxy(MyRemoteInterface.class);
 * int result = service.sum(3,4); //==> parameters ordered by position {params:[3,4]}
 * </pre>
 * <p>
 * Ex3:
 * <pre>
 * {@literal @}Remote
 * interface MyRemoteInterface{
 * 		
 * 	{@literal @}RemoteParams({"x","y"})
 * 	public int  sum(int x, int y);
 * }
 * 
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = rpc.createProxy(MyRemoteInterface.class);
 * int result = service.sum(3,4); //==> parameters ordered by name {...,params:{x:3,y:4}}
 * </pre>
 * <p>
 * Ex4:
 * <pre>
 * {@literal @}Remote
 * interface MyRemoteInterface{
 * 		
 *  {@literal @}RemoteProcName("my_sum")
 *  public int sum(int x, int y);
 * }
 * 
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * MyRemoteInterface service = rpc.createProxy(MyRemoteInterface.class);
 * int result = service.sum(3,4); //==> parameters ordered by position {method:"my_sum",params:[3,4]}
 * </pre>
 * <p>
 * There exists another way to make remote invocations, the direct api. The direct api uses variable arguments (Object...) 
 * to supply the parameters to the remote procedure. The direct api is suposed to be used as a delegated object inside your service 
 * implementation:
 *  
 * <pre>
 * public class MyService{
 * 	
 *  private Rpc rpcDelegate = new Rpc("http://myserver.mydomain.com:4444/rpc");	
 * 
 *  public int sum(int x, int y){
 *  	
 *   return rpcDelegate.callInteger("my_sum",x,y);
 *  }
 * 
 * </pre>
 * <p>
 * <strong>Some examples using the "direct call" api:</strong>
 * <p>
 * Ex5:
 * <pre>
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * int result = rpc.callInteger("sum",1,2,3,4,5);
 * </pre>
 * 
 * <p>
 * Ex6:
 * <pre>
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * rpc.callVoid("doTask");
 * </pre>
 * <p>
 * Ex7:
 * <pre>
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * int result = rpc.callInteger("sum",Rpc.RemoteParam("op1", 4),Rpc.RemoteParam("op2", 5)); //==> parameters ordered by name {...,params:{op1:4,op2:5}}
 * </pre>
 * <p>
 * Ex8:
 * <pre>
 * Rpc rpc = new Rpc("http://myserver.mydomain.com:4444/rpc");
 * short[] ids = rpc.callShortArray("get_ids");
 * </pre>
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 *
 */
public abstract class RpcImpl{
			
	/**
	 * Builder to create http requests.
	 */
	protected HttpRequestBuilder builder;
	
	/**
	 * Map that maps from int to Throwable.
	 */
	protected Map<Integer, Class<? extends RuntimeException>> exceptions = new HashMap<Integer, Class<? extends RuntimeException>>();
		
	/**
	 * Instantiates a new facade Rpc object.
	 * @param endpoint Remote procedure endpoint String.
	 * @throws MalformedURLException When the remote url is not valid: bad protocol, bad url syntax.
	 */
	public RpcImpl( String endpoint ) throws MalformedURLException{
		
		this.builder = new HttpRequestBuilder().endpoint(endpoint);		
	}
	
	/**
	 * Instantiates a new facade Rpc object.
	 * @param builder Builder object to create http requests at will.
	 */
	public RpcImpl( HttpRequestBuilder builder ){
		
		this.builder = builder;
	}
	
	/**
	 * Factory method to support in the creation of a new CallHandler.
	 * @return Call handler new instance.
	 */
	protected CallHandler createNewCallHandler() throws MalformedURLException{
				
		return new CallHandler( builder.build() );		
	}
						
	/**
	 * Calls the remote procedure and serializes the result JSONRPC field into an object of the class clazz. Generic call.
	 * @param method The remote method name.
	 * @param clazz The remote method's return type class type.
	 * @param params remote parameters supplied to the remote procedure.
	 * @return returned object from the remote procedure.
	 */
	protected <T> T call( CallHandler handler, String method, Class<T> clazz, Object params  ) throws IOException, InterruptedException {
		
		//JsonRpcRequest req = new JsonRpcRequest(method, params);
		//HttpRequest http = builder.build();	
		//handler.setHttpRequest(http);
		//String respStr = handler.perform( req.toString() );//blocking call				
		//JsonRpcResponse<T> res =  new JsonRpcResponse<T>( respStr, clazz );		
		JsonRpcRequest req = new JsonRpcRequest(method, params);
		JsonRpcResponse<T> res = handler.perform( req, clazz );//blocking call
				
		
		if(res.getError() != null){
			handler.setState( HttpRequest.State.FAILED );
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
		}
		handler.setState(HttpRequest.State.COMPLETE);
		return res.getResult();	
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
