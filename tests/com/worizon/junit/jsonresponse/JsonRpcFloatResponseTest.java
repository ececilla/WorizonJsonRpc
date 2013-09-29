package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcFloatResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 348739478.2749223234, \"id\": 2}";		
		JsonRpcResponse<Float> res = new JsonRpcResponse<Float>(message, Float.class);
				
		assertEquals( (Float)348739478.2749223234f, res.getResult() );
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		JsonRpcResponse<Float> res2 = new JsonRpcResponse<Float>(message, float.class);
		assertEquals( (Float)348739478.2749223234f, res2.getResult() );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\":null, \"id\": 2}";		
		JsonRpcResponse<Float> res = new JsonRpcResponse<Float>(message, Float.class);
		
		assertNull(res.getResult());
		
	}
	
}
