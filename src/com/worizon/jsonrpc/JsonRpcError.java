package com.worizon.jsonrpc;

import static com.worizon.jsonrpc.Const.Errors.INTERNAL_ERROR_CODE;
import static com.worizon.jsonrpc.Const.Errors.INVALID_PARAMS_CODE;
import static com.worizon.jsonrpc.Const.Errors.INVALID_REQUEST_CODE;
import static com.worizon.jsonrpc.Const.Errors.METHOD_NOT_FOUND_CODE;
import static com.worizon.jsonrpc.Const.Errors.PARSE_ERROR_CODE;

/**
 * This class encapsulates a JSON-RPC error. This library will make explicit distinction between two types of errors: 
 * <ul>
 * <li>JSON-RPC level errors: For spec's error codes.</li>
 * <li>Domain level errors: For non spec's error codes. </li>
 * </ul>
 * 
 * <p>
 * As fas as this library is concerned JSON-RPC errors are those that happen when something at the transport level 
 * goes wrong. These kind of errors include method not found, parse error or internal server error. So, most of these errors
 * are not generated inside the remote procedure but along the way to its execution.
 * 
 * <p>
 * On the other hand, domain level errors are those that are usually generated inside the remote procedure. 
 * When the remote procedure signals a failure situation, the server implementation should response back
 * with an error code different from those stated in the spec.
 * 
 * <p> 
 * Typically this object will be instantiated and filled with the contents of the field "error" from the JSON-RPC response message.
 * 
 * <p>
 * If the response error message contains some additional data about the failure situation it will be available through {@link JsonRpcError#getData()}. 
 * 
 * @author Enric Cecilla 
 * @since 1.0.0
 */
public class JsonRpcError {
	
	private int code;
	private String message;
	private Object data;
	
	/**
	 * Gets the code of the error. This field is mandatory for error responses.
	 * @return error code.
	 */
	public int getCode() {
		
		return code;
	}
	
	/**
	 * Checks whether this error object is a custom error or a JSON-RPC error.
	 * @return true if the error is a customlevel error.
	 */
	public boolean isCustomError(){
		
		switch( getCode() ){			
			case INVALID_REQUEST_CODE:
			case METHOD_NOT_FOUND_CODE:
			case INVALID_PARAMS_CODE:
			case INTERNAL_ERROR_CODE:
			case PARSE_ERROR_CODE:	
				return false;
			default:
				return true;
		}
	}
		
	
	/**
	 * Gets the message of this error. This field is mandatory for error responses.
	 */
	public String getMessage() {
		
		return message;
	}
	
	
	/**
	 * Gets the data of this error. This field is optional for error responses.
	 */
	public Object getData(){
		
		return data;
	}

	public JsonRpcError( int code, String message ){
		
		this.code = code;
		this.message = message;
	}
	
	public String toString(){
		
		if(data == null)
			return "{code:" + code + ", message:'" + message + "'}";
		else
			return "{code:" + code + ", message:'" + message + "', data:" + data +"}";
	}

}
