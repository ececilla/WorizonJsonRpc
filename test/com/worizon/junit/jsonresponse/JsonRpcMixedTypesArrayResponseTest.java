package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonSyntaxException;
import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcMixedTypesArrayResponseTest {
	

	@Test
	public void testResult1() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10.5,67], \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);			
		assertArrayEquals(new int[]{-19,10,67}, res.getResult());
	}
	
	@Test(expected=JsonSyntaxException.class)
	public void testResult2() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10.5,67,\"fooo\"], \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);			
		
	}
	
	@Test(expected=JsonSyntaxException.class)
	public void testResult3() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10.5,67,[]], \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);			
		
	}
	
	@Test(expected=JsonSyntaxException.class)
	public void testResult4() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10.5,67,{}], \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);			
		
	}
		
}
