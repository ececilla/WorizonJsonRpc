package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcStringResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": \"test message\", \"id\": 2}";		
		JsonRpcResponse<String> res = new JsonRpcResponse<String>(message, String.class);
				
		assertEquals( "test message", res.getResult() );	
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<String> res = new JsonRpcResponse<String>(message, String.class);
		
		assertNull(res.getResult());
		
	}
	
}
