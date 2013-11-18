package com.worizon.jsonrpc;

/**
 * Constants for JSONRPC low level code errors. The error object contains two fields: code and message.
 * JSONRPC spec has several code numbers reserved to signal different transport errors and 
 * server implementations which honors JSONRPC 2.0 SPEC must use these error codes.
 * 
 * 
 * @see <a href="http://www.jsonrpc.org/specification#error_object">JSONRPC 2.0 error codes</a>
 * @author Enric Cecilla
 * @since 1.0.0
 */
public final class Consts {
	
	private Consts(){}
	
	public static final int INVALID_REQUEST_CODE = -32600;
	public static final int METHOD_NOT_FOUND_CODE = -32601;
	public static final int INVALID_PARAMS_CODE = -32602;
	public static final int INTERNAL_ERROR_CODE = -32603;
	public static final int PARSE_ERROR_CODE = -32700;
	

}
