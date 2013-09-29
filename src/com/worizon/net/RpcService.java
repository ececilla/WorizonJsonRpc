package com.worizon.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

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
public class RpcService extends Observable {
	
	
	private  HttpRequester http;
	private  Map<Long,JsonRpcResponse<?>> aResp = new HashMap<Long,JsonRpcResponse<?>>();
		
	
	public RpcService( String endpoint ){
		
		this.http = new HttpRequester(endpoint);
	}
		
		
	public synchronized <T> JsonRpcResponse<T> sendSyncRequest( JsonRpcRequest req, Class<T> clazz ) throws IOException, InstantiationException, IllegalAccessException, InterruptedException{
		
		String response = http.sendSyncPostRequest( req.toString() );		
		return new JsonRpcResponse<T>( response, clazz );
				
	}
	
	
	
	public synchronized JsonRpcResponse<?> getResponseByid( Long id ){
		
		JsonRpcResponse<?> resp = aResp.get( id );
		aResp.remove( id );
		return resp;
	}
	
		
}
