package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcIntegerResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": -19, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
				
		assertEquals( -19, res.getResult().intValue() );	
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());				
		assertNull(res.getError());
		
		JsonRpcResponse<Integer> res2 = new JsonRpcResponse<Integer>(message, int.class);
		assertEquals( -19, res2.getResult().intValue() );
						
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		
		assertNull(res.getResult());
	}
			

}
