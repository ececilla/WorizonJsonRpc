package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcBooleanArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [1,1,false,true,0], \"id\": 2}";		
		JsonRpcResponse<boolean[]> res = new JsonRpcResponse<boolean[]>(message, boolean[].class);
		
		assertTrue(Arrays.equals(new boolean[]{true,true,false,true,false}, res.getResult()));		
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [1,1,false,true,0,null], \"id\": 2}";		
		JsonRpcResponse<Boolean[]> res2 = new JsonRpcResponse<Boolean[]>(message2, Boolean[].class);
		assertTrue(Arrays.equals(new Boolean[]{true,true,false,true,false,null}, res2.getResult()));		
		
	}
	
	@Test
	public void testNullResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<double[]> res = new JsonRpcResponse<double[]>(message, double[].class);
		assertNull(res.getResult());
		
	}
		
}
