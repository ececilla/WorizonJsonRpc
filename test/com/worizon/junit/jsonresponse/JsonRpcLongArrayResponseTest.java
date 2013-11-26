package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcLongArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67], \"id\": 2}";		
		JsonRpcResponse<long[]> res = new JsonRpcResponse<long[]>(message, long[].class);
	
		assertArrayEquals( new long[]{-19l,10l,54l,67l}, res.getResult());
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67,null], \"id\": 2}";		
		JsonRpcResponse<Long[]> res2 = new JsonRpcResponse<Long[]>(message2, Long[].class);
		assertArrayEquals( new Long[]{-19l,10l,54l,67l,null}, res2.getResult());
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<long[]> res = new JsonRpcResponse<long[]>(message, long[].class);
		
		assertNull(res.getResult());
	}
		
}
