package com.worizon.jsonrpc;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worizon.jsonrpc.gson.BooleanTypeAdapter;
import com.worizon.jsonrpc.gson.NonExposeExclusionEstrategy;

/**
 * 
 * Base class for JsonRpc request and response objects. 
 * 
 * @author enric
 *
 */
public abstract class JsonRpc {
	
	private static BooleanTypeAdapter booleanTypeAdapter = new BooleanTypeAdapter(); 
	private static ExclusionStrategy nonExposeStrategy = new NonExposeExclusionEstrategy();	
	private static GsonBuilder encodingBuilder = new GsonBuilder();
	private static GsonBuilder decodingBuilder = new GsonBuilder();
	static{
		
		encodingBuilder.setExclusionStrategies(nonExposeStrategy);
		encodingBuilder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		encodingBuilder.registerTypeAdapter(boolean.class, booleanTypeAdapter);		
		decodingBuilder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		decodingBuilder.registerTypeAdapter(boolean.class, booleanTypeAdapter);
	}
	
	protected String jsonrpc = null;	
	protected Long id = null;
		
	public JsonRpc(){
				
	}
	
	public JsonRpc( String version, Long id ){
		
		jsonrpc = version;
		this.id = id;
	}		
	
	protected static Gson getSerializeHelper(){
				
		return encodingBuilder.create();
	}
	
	protected static Gson getDeserializeHelper(){
				
		return decodingBuilder.create();
	}
		
	public String getVersion() {
		
		return jsonrpc;
	}

	public void setVersion( String jsonrpc ) {
		
		this.jsonrpc = jsonrpc;
	}

	public Long getId() {
		
		return id;
	}

	public void setId( Long id ) {
		
		this.id = id;
	}
	
}
