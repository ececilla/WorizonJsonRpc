package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcDoubleResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 348739478.2749223234, \"id\": 2}";		
		JsonRpcResponse<Double> res = new JsonRpcResponse<Double>(message, Double.class);
				
		assertThat( res.getResult(), is(348739478.2749223234d));
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		JsonRpcResponse<Double> res2 = new JsonRpcResponse<Double>(message, double.class);
		assertThat( res2.getResult(), is(348739478.2749223234d) );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		
		JsonRpcResponse<Double> res = new JsonRpcResponse<Double>(message, Double.class);		
		assertThat(res.getResult(), is(nullValue()));
	}
	
}
