package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcIntegerArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67], \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);
	
		assertArrayEquals( new int[]{-19,10,54,67}, res.getResult());
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67,null], \"id\": 2}";		
		JsonRpcResponse<Integer[]> res2 = new JsonRpcResponse<Integer[]>(message2, Integer[].class);
		assertArrayEquals( new Integer[]{-19,10,54,67,null}, res2.getResult());
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);
		
		assertNull(res.getResult());
	}
		
}
