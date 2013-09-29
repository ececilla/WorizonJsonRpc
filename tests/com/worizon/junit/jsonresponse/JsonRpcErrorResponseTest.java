package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcErrorResponseTest {
	

	@Test
	public void testError() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":\"test error\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertEquals( -100, res.getError().getCode() );
		assertEquals("test error", res.getError().getMessage());		
		assertNull(res.getResult());
		
	}	
	
}
