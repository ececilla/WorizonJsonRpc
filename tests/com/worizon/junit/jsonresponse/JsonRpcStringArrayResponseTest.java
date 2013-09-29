package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcStringArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [\"one\",\"two\",\"three\",\"four\"], \"id\": 2}";		
		JsonRpcResponse<String[]> res = new JsonRpcResponse<String[]>(message, String[].class);
	
		assertArrayEquals( new String[]{"one","two","three","four"}, res.getResult());
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [\"one\",\"two\",\"three\",\"four\",null], \"id\": 2}";		
		JsonRpcResponse<String[]> res2 = new JsonRpcResponse<String[]>(message2, String[].class);
		assertArrayEquals( new String[]{"one","two","three","four",null}, res2.getResult());
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<String[]> res = new JsonRpcResponse<String[]>(message, String[].class);
		
		assertNull(res.getResult());
		
	}
		
}
