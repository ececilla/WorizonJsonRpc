package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonSyntaxException;
import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcMixedTypesArrayResponseTest {
	

	@Test(expected = JsonSyntaxException.class)
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10.5,67], \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);			
		
	}
		
}
