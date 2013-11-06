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
 * @author Enric Cecilla
 *
 */
public abstract class JsonRpc {
	
	private static BooleanTypeAdapter booleanTypeAdapter = new BooleanTypeAdapter(); 
	private static ExclusionStrategy nonExposeStrategy = new NonExposeExclusionEstrategy();	
	private static GsonBuilder serializeBuilder = new GsonBuilder();
	private static GsonBuilder deserializeBuilder = new GsonBuilder();
	static{
		
		serializeBuilder.setExclusionStrategies(nonExposeStrategy);
		serializeBuilder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		serializeBuilder.registerTypeAdapter(boolean.class, booleanTypeAdapter);		
		deserializeBuilder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		deserializeBuilder.registerTypeAdapter(boolean.class, booleanTypeAdapter);
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
				
		return serializeBuilder.create();
	}
	
	protected static Gson getDeserializeHelper(){
				
		return deserializeBuilder.create();
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
