package com.worizon.jsonrpc;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * Clase base para las clases request y response del protocolo json-rpc.
 * 
 * @author enric
 *
 */
public abstract class JsonRpc {
	
	private static BooleanTypeAdapter booleanTypeAdapter = new BooleanTypeAdapter(); 
	private static ExclusionStrategy nonExposeStrategy = new NonExposeExclusionEstrategy();		
	
	protected String jsonrpc = null;	
	protected Long id = null;
		
	public JsonRpc(){
				
	}
	
	public JsonRpc( String version, Long id ){
		
		jsonrpc = version;
		this.id = id;
	}		
	
	public static Gson getEncodingGson(){
		
		GsonBuilder builder = new GsonBuilder();
		builder.setExclusionStrategies(nonExposeStrategy);
		builder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		builder.registerTypeAdapter(boolean.class, booleanTypeAdapter);
		return builder.create();
	}
	
	public static Gson getDecodingGson(){
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Boolean.class, booleanTypeAdapter);
		builder.registerTypeAdapter(boolean.class, booleanTypeAdapter);
		return builder.create();
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
