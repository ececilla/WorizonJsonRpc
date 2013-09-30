package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcBooleanResponseTest {
	

	@Test
	public void testResultTrue() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": true, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertEquals( true, res.getResult() );	
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertEquals( true, res2.getResult() );
		
	}
	
	@Test
	public void testResult1() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 1, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertEquals( true, res.getResult() );	
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertEquals( true, res2.getResult() );
		
	}
	
	@Test
	public void testResultFalse() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": false, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertEquals( false, res.getResult() );	
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertEquals( false, res2.getResult() );
		
	}
	
	@Test
	public void testResult0() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 0, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertEquals( false, res.getResult() );	
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertEquals( false, res2.getResult() );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<String> res = new JsonRpcResponse<String>(message, String.class);
		
		assertNull(res.getResult());
		
	}
	
}
