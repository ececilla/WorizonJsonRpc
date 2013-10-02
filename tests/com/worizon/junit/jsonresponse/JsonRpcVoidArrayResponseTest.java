package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcVoidArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [{},{},{},{},{}], \"id\": 2}";		
		JsonRpcResponse<Void[]> res = new JsonRpcResponse<Void[]>(message, Void[].class);
		
		System.out.println( res.getResult()[0] );
		//assertTrue(Arrays.equals(new boolean[]{true,true,false,true,false}, res.getResult()));		
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());				
		
	}
	
	@Test
	public void testNullResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<Void[]> res = new JsonRpcResponse<Void[]>(message, Void[].class);
		assertNull(res.getResult());
		
	}
		
}
