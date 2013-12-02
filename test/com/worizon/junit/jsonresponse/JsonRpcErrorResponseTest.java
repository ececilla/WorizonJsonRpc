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
	public void testIsCustomErrorTrue() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":\"test error\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertEquals( -100, res.getError().getCode() );
		assertEquals("test error", res.getError().getMessage());
		assertEquals("{code:-100, message:'test error'}", res.getError().toString() );
		assertTrue(res.getError().isCustomError());
		assertNull(res.getResult());		
	}
	
	@Test
	public void testIsCustomErrorFalse() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-32601, \"message\":\"method not found\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertEquals( -32601, res.getError().getCode() );
		assertEquals("method not found", res.getError().getMessage());
		assertEquals("{code:-32601, message:'method not found'}", res.getError().toString() );
		assertFalse(res.getError().isCustomError());
		assertNull(res.getResult());		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorAndResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {}, \"result\":{}, \"id\": 2}";		
		JsonRpcResponse<Void> res = new JsonRpcResponse<Void>(message, Void.class);												
	}
	
		
	@Test(expected=JsonRpcException.class)
	public void testErrorWithMessageNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":null}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertEquals( -100, res.getError().getCode() );
		assertNull(res.getError().getMessage());				
		assertEquals("{code:-100, message:'null'}", res.getError().toString() );
		assertNull(res.getResult());		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorWithCodeNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":null, \"message\":\"Not found\"}, \"id\": 2}";		
		new JsonRpcResponse<Integer>(message, Integer.class);
				
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorWithMessageNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100}, \"id\": 2}";		
		new JsonRpcResponse<Integer>(message, Integer.class);
				
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorWithCodeNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"message\":null}, \"id\": 2}";		
		new JsonRpcResponse<Void>(message, Void.class);
		
	}		
	
}
