package com.worizon.jsonrpc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;



/**
 * This class is the representation of a JSON-RPC request. The id field of the JSON-RPC
 * message is generated with a timestamp value as returned by
 * 
 * @author enric
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class JsonRpcRequest extends JsonRpcBase implements Serializable {
			
	/**
	 * Remote procedure method name.
	 */
	private final String method;		
	
	/**
	 * Remote procedure parameters, this object might be both a List or a
	 * Map.
	 */
	private Object params;
	
	JsonRpcRequest( String method, Object params){
				
		super("2.0",IDGenerator.getInstance().incrementAndGet());
		this.method = method;
		this.params = params;
	}
	
	/**
	 * Copy constructor.
	 */
	public JsonRpcRequest( JsonRpcRequest req ){
		
		super("2.0", req.getId());
		this.method = req.getMethod();
		this.params = req.getParams();
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
			
	/**
	 * Returns remote method name at which the call will be targeted.
	 * @return The remote name.
	 */
	public String getMethod() {
		
		return method;
	}
	
	/**
	 * Returns arguments supplied to the remote procedure, might be Map or List.
	 */
	public Object getParams() {
		
		return params;
	}
		
	
	@Override
	public boolean equals( Object obj ){
		
		if(obj == null)
			return false;
		else if(obj == this)
			return true;
		else if( !(obj instanceof JsonRpcRequest) )
			return false;		
				
		JsonRpcRequest req = (JsonRpcRequest)obj;
		return id.equals(req.getId()); 
		/*		
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
		*/										 
	}
	
	/**
	 * This Helper method turns a String representing a JSON-RPC request into a JsonRpcRequest object.
	 * 
	 * Ex:
	 * "{\"method\":\"test\",\"params\":{\"a\":10,\"n\":20},\"jsonrpc\":\"2.0\",id:887237382}"
	 * 
	 * @return  The object which holds the request information.
	 */
	public static JsonRpcRequest parse( String request ){
		
		JsonRpcRequest req = getDeserializeHelper().fromJson(request, JsonRpcRequest.class);
		if(req.getId() == null || req.getMethod() == null || req.getVersion() == null)
			throw new IllegalArgumentException("Missing fields in this request");
		
		return req;
	}
	
	/**
	 * Converts this object to a readable String.
	 * @return The string representation of this request object.
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		
		Gson gson = getSerializeHelper();		
		return gson.toJson(this);
	}
	
}
