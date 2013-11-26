package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcLongResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 3487394782749223234, \"id\": 2}";		
		JsonRpcResponse<Long> res = new JsonRpcResponse<Long>(message, Long.class);
				
		assertEquals( (Long)3487394782749223234L, res.getResult() );
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		JsonRpcResponse<Long> res2 = new JsonRpcResponse<Long>(message, long.class);
		assertEquals( (Long)3487394782749223234L, res2.getResult() );
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<Long> res = new JsonRpcResponse<Long>(message, Long.class);
		
		assertNull(res.getResult());
		
	}
	
}
