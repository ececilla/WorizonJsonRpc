package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcIntegerArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67], \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);
	
		assertThat( new int[]{-19,10,54,67}, is(equalTo(res.getResult())));
		assertThat( res.getId().longValue(), is(2L) );				
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67,null], \"id\": 2}";		
		JsonRpcResponse<Integer[]> res2 = new JsonRpcResponse<Integer[]>(message2, Integer[].class);
		assertThat( new Integer[]{-19,10,54,67,null}, is(equalTo(res2.getResult())));
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<int[]> res = new JsonRpcResponse<int[]>(message, int[].class);
		
		assertThat(res.getResult(), is(nullValue()));
	}
		
}
