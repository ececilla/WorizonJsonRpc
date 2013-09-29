package com.worizon.jsonrpc;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Clase que representa una respuesta de JSON-RPC 2.0.
 * 
 * Ejemplos de respuesta JSON-RPC:
 * 
 * Con error:
 * {"jsonrpc": "2.0", "error": {"code": -32601, "message": "Method not found."}, "id": "1"}
 * 
 * Sin error:
 * {"jsonrpc": "2.0", "result": 19, "id": 1}
 * 
 * @author enric
 *
 * @param <R>
 */
public class JsonRpcResponse<T> extends JsonRpc {	
		
	private T result = null;
	private JsonRpcError error = null;	
	
	public JsonRpcResponse( String json_str, Class<T> clazz ) throws InstantiationException, IllegalAccessException {
				
		JsonParser parser = new JsonParser();
		JsonObject root = parser.parse(json_str).getAsJsonObject();				
		jsonrpc = root.get("jsonrpc").getAsString();		

		if( !root.get("id").isJsonNull() )
			id = root.get("id").getAsLong();
		
		Gson gson = getDecodingGson();
		if( root.has("result") ){
			if( !root.get("result").isJsonNull() ){ 
				String result_str = gson.toJson(root.get("result"));					
				result = gson.fromJson(result_str, clazz);
			}else
				result = null;			
		}else{
			
			String error_str = gson.toJson(root.get("error"));
			error = gson.fromJson(error_str, JsonRpcError.class);
		}
	}			
	
	
	public T getResult(){
		
		return result;
	}
	
	public JsonRpcError getError(){
		
		return error;
	}
			
}
