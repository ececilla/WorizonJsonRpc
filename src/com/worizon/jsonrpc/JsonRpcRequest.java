package com.worizon.jsonrpc;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



/**
 * Clase que representa una request de JSON-RPC version 2.0
 * 
 * @author enric
 *
 */
@SuppressWarnings("serial")
public class JsonRpcRequest extends JsonRpc implements Serializable {
			

	private final String method;	
	private Map<String, Object> params;
	
	
	public JsonRpcRequest( String method, Map<String, Object> params ){
		
		super("2.0", System.currentTimeMillis() );		
		this.params = params;
		this.method = method;
		
	}
	
	public JsonRpcRequest( String method ){
		
		this(method, new LinkedHashMap<String, Object>());		

	}

	
	public JsonRpcRequest( int depth, Map<String, Object> params ){
		
		this(Thread.currentThread().getStackTrace()[depth].getMethodName(), params);
		
	}
	
	public JsonRpcRequest( int depth ){
		
		this( Thread.currentThread().getStackTrace()[depth].getMethodName() );
	}
	
	
	
	public String getMethod() {
		
		return method;
	}
		
	public Map<String, Object> getParams() {
		
		return params;
	}
	
	
	public void addParam(String key, Object value){
		
		params.put(key, value);
	}
	
	@Override
	public boolean equals( Object obj ){
		try{
			
			JsonRpcRequest req = (JsonRpcRequest)obj;
			return req.id.longValue() == id.longValue();
		}catch( ClassCastException cce){
			return false;
		}		 
	}
	
	
	public String toString(){
		
		Gson gson = getEncodingHelper();		
		return gson.toJson(this);
	}
	
}
