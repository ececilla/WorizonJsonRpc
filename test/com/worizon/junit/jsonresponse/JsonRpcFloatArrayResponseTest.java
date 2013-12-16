package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcFloatArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1], \"id\": 2}";		
		JsonRpcResponse<float[]> res = new JsonRpcResponse<float[]>(message, float[].class);
	
		assertThat( new float[]{-19.8f,10.2f,54.9f,67.1f}, is(equalTo(res.getResult())));		
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1,null], \"id\": 2}";		
		JsonRpcResponse<Float[]> res2 = new JsonRpcResponse<Float[]>(message2, Float[].class);
		assertThat( new Float[]{-19.8f,10.2f,54.9f,67.1f,null}, is(equalTo(res2.getResult())));
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<float[]> res = new JsonRpcResponse<float[]>(message, float[].class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
		
}
