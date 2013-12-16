package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcFloatResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 348739478.2749223234, \"id\": 2}";		
		JsonRpcResponse<Float> res = new JsonRpcResponse<Float>(message, Float.class);
				
		assertThat( res.getResult(), is(348739478.2749223234f) );
		assertThat( res.getId().longValue(), is(2L) );				
		assertThat( res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		JsonRpcResponse<Float> res2 = new JsonRpcResponse<Float>(message, float.class);
		assertThat( res2.getResult(), is(348739478.2749223234f) );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\":null, \"id\": 2}";		
		JsonRpcResponse<Float> res = new JsonRpcResponse<Float>(message, Float.class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
	
}
