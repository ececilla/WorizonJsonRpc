package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcStringArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [\"one\",\"two\",\"three\",\"four\"], \"id\": 2}";		
		JsonRpcResponse<String[]> res = new JsonRpcResponse<String[]>(message, String[].class);
	
		assertThat( res.getResult(),  is(new String[]{"one","two","three","four"}) );
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is("2.0"));
		assertThat(res.getError(), is(nullValue()));
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [\"one\",\"two\",\"three\",\"four\",null], \"id\": 2}";		
		JsonRpcResponse<String[]> res2 = new JsonRpcResponse<String[]>(message2, String[].class);
		assertThat(res2.getResult(), is(new String[]{"one","two","three","four",null}) );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<String[]> res = new JsonRpcResponse<String[]>(message, String[].class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
		
}
