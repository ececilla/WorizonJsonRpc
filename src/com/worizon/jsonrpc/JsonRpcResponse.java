package com.worizon.jsonrpc;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


/**
 * This class represents a JSON-RPC response. The generic parameter T is the type of the
 * expected output of the remote procedure. For instance, JsonRpcResponse{@literal <}Integer{@literal >} is the response
 * object of a remote procedure returning an integer. Not only this class can be parameterized with
 * primitive types but also with custom types, ex: JsonRpcResponse{@literal <}Dummy{@literal >}.
 * 
 * <p>
 * All JsonRpcResponse MUST come with one and only one of the fields error or results filled. A server response having 
 * none of these or both of them will throw a JsonRpcException.
 *  
 * <p> 
 * If an error happens, on the remote procedure or along the way to the remote procedure, the error object 
 * will be non-null and the result object will be null, otherwise the result object will be non-null.
 * 
 * <p>
 * Example of two JSON-RPC response strings, error and non-error situation:
 * <pre>
 *   
 * {"jsonrpc": "2.0", "error": {"code": -32601, "message": "Method not found."}, "id": 188329}
 * 
 * {"jsonrpc": "2.0", "result": 19, "id": 177784}
 * </pre>
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 *
 */
public class JsonRpcResponse<T> extends JsonRpc {	
	
	/**
	 * Result object.
	 */
	private T result = null;
	
	/**
	 * Error object.
	 */
	private JsonRpcError error = null;	
	
	/**
	 * Crates a JsonRpcResponse object. 
	 * @param jsonStr The JSON-RPC response string from the server.
	 * @param clazz The type that the result object will be converted to.
	 * @throws JsonRpcException when the jsonrpc field in the response is different from "2.0".
	 * @throws JsonRpcException when the id field in the response is empty or null.
	 * @throws JsonRpcExceptoin when result and error fields are both empty or null.
	 */
	public JsonRpcResponse( String jsonStr, Class<? extends T> clazz ) {
				
		JsonParser parser = new JsonParser();
		JsonObject root = parser.parse(jsonStr).getAsJsonObject();				
		jsonrpc = root.get("jsonrpc").getAsString();
		if(!jsonrpc.equals("2.0"))
			throw new JsonRpcException("Version not supported:" + jsonrpc);

		if( root.has("id") && !root.get("id").isJsonNull() )
			id = root.get("id").getAsLong();
		else
			throw new JsonRpcException("Id not found or null");
		
		Gson gson = getDeserializeHelper();
		if( root.has("result") ){
			
			if( root.has("error") )
				throw new JsonRpcException("Both result and error fields present");
			
			if( !root.get("result").isJsonNull() ){ 
								
				result = gson.fromJson(root.get("result"), clazz);				
			}else
				result = null;			
		}else if( root.has("error") ){
									
			if(!root.get("error").isJsonNull() ){								
				
				JsonElement errorJsonElement = root.get("error");		
				if( errorJsonElement.getAsJsonObject().has("code") && errorJsonElement.getAsJsonObject().has("message")){
					
					if( !errorJsonElement.getAsJsonObject().get("code").isJsonNull() && 
						!errorJsonElement.getAsJsonObject().get("message").isJsonNull()){											
					
						error = gson.fromJson(errorJsonElement, JsonRpcError.class);
					}else
						throw new JsonRpcException("Error code or message should not be null");
				}else
					throw new JsonRpcException("Error code or message not found");				
			}else
				throw new JsonRpcException("Error field is null");			
		}else
			throw new JsonRpcException("Neither result nor error fields present");
		
	}			
	
	/**
	 * Gets the result part of this JsonRpcResponse object.
	 * @return Result object.
	 */
	public T getResult(){
		
		return result;
	}
	
	/**
	 * Gets the error part of this JsonRpcResponse object.
	 * @return Error object.
	 */
	public JsonRpcError getError(){
		
		return error;
	}
			
}
