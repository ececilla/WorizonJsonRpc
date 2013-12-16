package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcVoidResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": {}, \"id\": 2}";		
		JsonRpcResponse<Void> res = new JsonRpcResponse<Void>(message, Void.class);
		
		assertThat(res.getResult() instanceof Void, is(true) );					
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is("2.0"));
		assertThat( res.getError(), is(nullValue()) );				
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		
		JsonRpcResponse<Void> res = new JsonRpcResponse<Void>(message, Void.class);		
		assertThat(res.getResult(), is(nullValue()));
	}
	
}
