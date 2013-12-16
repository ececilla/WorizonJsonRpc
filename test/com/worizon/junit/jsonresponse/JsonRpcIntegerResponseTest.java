package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcIntegerResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": -19, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
				
		assertThat( res.getResult(), is(-19) );	
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is(equalTo("2.0")));				
		assertThat(res.getError(), is(nullValue()));
		
		JsonRpcResponse<Integer> res2 = new JsonRpcResponse<Integer>(message, int.class);
		assertThat( res2.getResult(), is(-19) );
						
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		
		assertThat(res.getResult(), is(nullValue()));
	}
			

}
