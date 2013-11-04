package com.worizon.jsonrpc;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



/**
 * 
 * 
 * @author enric
 *
 */
@SuppressWarnings("serial")
public class JsonRpcRequest extends JsonRpc implements Serializable {
			

	private final String method;		
	private Object params;
	
	JsonRpcRequest( String method, Object params){
		
		super("2.0",System.currentTimeMillis());
		this.method = method;
		this.params = params;
	}
	
	public JsonRpcRequest( String method ){
		
		this(method, (Object)null);
	}
	
	
	public JsonRpcRequest( String method, Map<String, Object> params  ){
		
		this(method, (Object)params);
	}
	
	public JsonRpcRequest( String method, List<Object> params){
		
		this(method, (Object)params);
	}			
			
	
	public String getMethod() {
		
		return method;
	}
	
	/*
	public Object getParams() {
		
		return params;
	}
	
	
	public void addParam(String key, Object value){
		
		params.put(key, value);
	}
	*/
	
	@Override
	public boolean equals( Object obj ){
		
		if(obj == null)
			return false;
		else if(obj == this)
			return true;
		else if( !(obj instanceof JsonRpcRequest) )
			return false;
		
		
			
		JsonRpcRequest req = (JsonRpcRequest)obj;
		boolean retval = req.method.equals(this.method);
		if(!retval)
			return false;
		
		retval =  req.id.longValue() == id.longValue();
		if(!retval)
			return false;
		
		if( params instanceof Map){
			for( Object key: ((Map)params).keySet() ){
				
				retval = ((Map) params).get(key).equals( ((Map)req.params).get(key) );
				if(!retval)
					return false;
			}
		}
		
		return retval;
			
						
			 
	}
	
	public static JsonRpcRequest parse( String request ){
		
		JsonRpcRequest req = getDecodingHelper().fromJson(request, JsonRpcRequest.class);
		return req;
	}
	
	public String toString(){
		
		Gson gson = getEncodingHelper();		
		return gson.toJson(this);
	}
	
}
