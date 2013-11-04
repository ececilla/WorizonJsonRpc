package com.worizon.jsonrpc;

/**
 * This class encapsulates a json rpc error.Errors can be jsonrpc level or domain level errors.
 * 
 * @author ececilla
 */
public class JsonRpcError {
	
	private int code;
	private String message;
	private Object data;
	
	public int getCode() {
		
		return code;
	}

	public void setCode(int code) {
		
		this.code = code;
	}

	public String getMessage() {
		
		return message;
	}

	public void setMessage(String message) {
		
		this.message = message;
	}
	
	public Object getData(){
		
		return data;
	}

	public JsonRpcError( int code, String message ){
		
		this.code = code;
		this.message = message;
	}
	
	public String toString(){
		
		return "{code:" + code + ",message:" + message + "}";
	}

}
