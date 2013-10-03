package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcDoubleListResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1], \"id\": 2}";		
		JsonRpcResponse<List> res = new JsonRpcResponse<List>(message, List.class);
		
		
		assertArrayEquals( new Double[]{-19.8d,10.2d,54.9d,67.1d}, res.getResult().toArray());		
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());			
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<float[]> res = new JsonRpcResponse<float[]>(message, float[].class);
		
		assertNull(res.getResult());
		
	}
		
}
