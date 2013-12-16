package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcDoubleListResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1], \"id\": 2}";		
		JsonRpcResponse<List> res = new JsonRpcResponse<List>(message, List.class);
		
		
		assertThat( new Double[]{-19.8d,10.2d,54.9d,67.1d}, is(equalTo(res.getResult().toArray())));		
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));			
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<float[]> res = new JsonRpcResponse<float[]>(message, float[].class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
		
}
