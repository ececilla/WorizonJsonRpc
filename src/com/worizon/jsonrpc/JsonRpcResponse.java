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
	
	public JsonRpcResponse( String json_str, Class<? extends T> clazz ) throws InstantiationException, IllegalAccessException, JsonRpcException {
				
		JsonParser parser = new JsonParser();
		JsonObject root = parser.parse(json_str).getAsJsonObject();				
		jsonrpc = root.get("jsonrpc").getAsString();
		if(!jsonrpc.equals("2.0"))
			throw new JsonRpcException("Version not supported:" + jsonrpc);

		if( root.has("id") && !root.get("id").isJsonNull() )
			id = root.get("id").getAsLong();
		else
			throw new JsonRpcException("Id not found or null");
		
		Gson gson = getDeserializeHelper();
		if( root.has("result") ){
			if( !root.get("result").isJsonNull() ){ 
				
				result = gson.fromJson(root.get("result"), clazz);
			}else
				result = null;			
		}else if( root.has("error") ){
			if(!root.get("error").isJsonNull()){
				
				JsonObject jsonError = root.get("error").getAsJsonObject();
				if( jsonError.has("code") && jsonError.has("message")){
					
					String error_str = gson.toJson(root.get("error"));
					error = gson.fromJson(error_str, JsonRpcError.class);
				}else{
					throw new JsonRpcException("Error code or message not found");
				}
			}else{
				throw new JsonRpcException("Error field is null");
			}
		}else{
			throw new JsonRpcException("Neither result nor error fields present");
		}
	}			
	
	
	public T getResult(){
		
		return result;
	}
	
	public JsonRpcError getError(){
		
		return error;
	}
			
}
