package com.worizon.jsonrpc;

import static com.worizon.jsonrpc.Const.Errors.INTERNAL_ERROR_CODE;
import static com.worizon.jsonrpc.Const.Errors.INVALID_PARAMS_CODE;
import static com.worizon.jsonrpc.Const.Errors.INVALID_REQUEST_CODE;
import static com.worizon.jsonrpc.Const.Errors.METHOD_NOT_FOUND_CODE;
import static com.worizon.jsonrpc.Const.Errors.PARSE_ERROR_CODE;

/**
 * This class encapsulates a JSON-RPC error. This library will makes explicit distinction of two types of errors: 
 * -JSON-RPC level errors.
 * -Domain level errors.
 * 
 * JSONRPC errors are those that happen when something at the transport level went wrong. These kind of
 * errors include method not found, parse error or internal server error. So, most of these errors are not generated
 * inside the remote procedure but along the way to its execution.
 * 
 * On the other hand, domain level errors are those that are usually generated inside the remote procedure. 
 * When the remote procedure signals a failure situation, the server implementation should response back
 * with an error code different from those stated in the spec. 
 *  
 * 
 * @author Enric Cecilla 
 * @since 1.0.0
 */
public class JsonRpcError {
	
	private int code;
	private String message;
	private Object data;
	
	/**
	 * Gets the code of the error.
	 * @return error code.
	 */
	public int getCode() {
		
		return code;
	}
	
	/**
	 * Checks if this error object is a domain level error.
	 * @return true if the error is a domain level error.
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
	 * Gets the message of this error.
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
