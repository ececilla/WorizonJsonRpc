package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcLongArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67], \"id\": 2}";		
		JsonRpcResponse<long[]> res = new JsonRpcResponse<long[]>(message, long[].class);
	
		assertThat( new long[]{-19l,10l,54l,67l}, is(equalTo(res.getResult())));
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(),is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [-19,10,54,67,null], \"id\": 2}";		
		JsonRpcResponse<Long[]> res2 = new JsonRpcResponse<Long[]>(message2, Long[].class);
		assertThat( new Long[]{-19l,10l,54l,67l,null}, is(equalTo(res2.getResult())));
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<long[]> res = new JsonRpcResponse<long[]>(message, long[].class);
		
		assertThat(res.getResult(), is(nullValue()));
	}
		
}
