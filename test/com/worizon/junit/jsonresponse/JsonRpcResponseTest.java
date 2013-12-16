package com.worizon.junit.jsonresponse;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcException;
import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcResponseTest {
	
	
	@Test(expected=JsonRpcException.class)
	public void testErrorNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": null, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);		
		
	}	
	
	@Test(expected=JsonRpcException.class)
	public void testErrorVersionNotSupported() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.1\", \"error\": {}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);		
		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testResultNotFoundErrorNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);		
		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testIdNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.1\", \"error\": {}, \"id\": null}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);		
		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testIdNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.1\", \"error\": {}}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);		
		
	}
	
}
