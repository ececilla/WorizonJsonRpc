package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcStringResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": \"test message\", \"id\": 2}";		
		JsonRpcResponse<String> res = new JsonRpcResponse<String>(message, String.class);
				
		assertThat( res.getResult(), is("test message"));	
		assertThat( res.getId(), is(2L) );				
		assertThat( res.getVersion(), is("2.0"));
		assertThat( res.getError(), is(nullValue()));
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<String> res = new JsonRpcResponse<String>(message, String.class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
	
}
