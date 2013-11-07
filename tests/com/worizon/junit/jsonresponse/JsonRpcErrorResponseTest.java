package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcException;
import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcErrorResponseTest {
	

	@Test
	public void testErrorWithCodeWithMessageWithData() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":\"test error\",\"data\":\"foo\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertEquals( -100, res.getError().getCode() );
		assertEquals("test error", res.getError().getMessage());	
		assertEquals("foo", (String)res.getError().getData());
		assertEquals("{code:-100, message:'test error', data:foo}", res.getError().toString() );
		assertNull(res.getResult());		
	}
	
	@Test
	public void testErrorWithCodeWithMessage() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":\"test error\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertEquals( -100, res.getError().getCode() );
		assertEquals("test error", res.getError().getMessage());
		assertEquals("{code:-100, message:'test error'}", res.getError().toString() );
		assertNull(res.getResult());		
	}
		
	@Test
	public void testErrorWithCodeMessageNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":null}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertEquals( -100, res.getError().getCode() );
		assertNull(res.getError().getMessage());				
		assertEquals("{code:-100, message:'null'}", res.getError().toString() );
		assertNull(res.getResult());		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorWithCodeMessageNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100}, \"id\": 2}";		
		new JsonRpcResponse<Integer>(message, Integer.class);
				
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorCodeNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"message\":null}, \"id\": 2}";		
		new JsonRpcResponse<Void>(message, Void.class);
		
	}
	
}
