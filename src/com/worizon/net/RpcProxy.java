package com.worizon.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.worizon.jsonrpc.JsonRpcException;
import com.worizon.jsonrpc.JsonRpcRequest;
import com.worizon.jsonrpc.JsonRpcResponse;
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
	
	public synchronized <T> T call( Map<String, Object> params, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, JsonRpcException{
		
		JsonRpcRequest req = new JsonRpcRequest(params);
		String response = http.sendSyncPostRequest( req.toString() );//blocking call
		JsonRpcResponse<T> res =  new JsonRpcResponse<T>( response, clazz );
		if(res.getError() != null)
			throw new JsonRpcException( res.getError() );
		else
			return res.getResult();		
	}
		
		
	public synchronized <T> JsonRpcResponse<T> sendSyncRequest( JsonRpcRequest req, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException{
		
		String response = http.sendSyncPostRequest( req.toString() );		
		return new JsonRpcResponse<T>( response, clazz );
				
	}			
		
}
