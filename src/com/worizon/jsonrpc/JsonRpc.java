package com.worizon.jsonrpc;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worizon.jsonrpc.gson.BooleanTypeAdapter;
import com.worizon.jsonrpc.gson.NonExposeExclusionEstrategy;

/**
 * 
 * Base class for JsonRpc request and response objects. Common fields for both request and response 
 * objects are defined here as well as static helpers methods.
 * 
 * @author Enric Cecilla
 *
 */
public abstract class JsonRpc {
	
	private static BooleanTypeAdapter booleanTypeAdapter = new BooleanTypeAdapter(); 
	private static ExclusionStrategy nonExposeStrategy = new NonExposeExclusionEstrategy();	
	private static GsonBuilder serializeGsonBuilder = new GsonBuilder();
	private static GsonBuilder deserializeGsonBuilder = new GsonBuilder();
	static{
		
		serializeGsonBuilder.setExclusionStrategies(nonExposeStrategy);
		serializeGsonBuilder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		serializeGsonBuilder.registerTypeAdapter(boolean.class, booleanTypeAdapter);		
		deserializeGsonBuilder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		deserializeGsonBuilder.registerTypeAdapter(boolean.class, booleanTypeAdapter);
	}
	
	protected String jsonrpc = null;	
	protected Long id = null;
		
	public JsonRpc(){
				
	}
	
	public JsonRpc( String version, Long id ){
		
		jsonrpc = version;
		this.id = id;
	}		
	
	/**
	 * Gets Gson main facade configured to serialize pojos into a json string
	 */
	protected static Gson getSerializeHelper(){
				
		return serializeGsonBuilder.create();
	}
	
	/**
	 * Gets Gson main facade configured to deserialize a json string into a pojo.
	 */
	protected static Gson getDeserializeHelper(){
				
		return deserializeGsonBuilder.create();
	}
	
	/**
	 * Gets the version of this request or response.
	 * @return the jsonrpc version. Should be "2.0"
	 */
	public String getVersion() {
		
		return jsonrpc;
	}

	/**
	 * Sets the version of the request or reponse.
	 * @param jsonrpc: 
	 */
	public void setVersion( String jsonrpc ) {
		
		this.jsonrpc = jsonrpc;
	}
	
	/**
	 * Gets the id of the request or response.
	 * @return The id as a timestamp. 
	 */
	public Long getId() {
		
		return id;
	}

	/**
	 * Sets the if of the request, response.
	 */
	public void setId( Long id ) {
		
		this.id = id;
	}
	
}
