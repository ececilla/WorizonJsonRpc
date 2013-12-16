package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcMapResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": {\"x\":1,\"y\":\"test\"}, \"id\": 2}";		
		JsonRpcResponse<Map> res = new JsonRpcResponse<Map>(message, Map.class);
				
		assertThat(res.getResult(), is(equalTo((Map)new HashMap(){{
			put("x",1d);
			put("y","test");
		}})));			
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is("2.0"));
		assertThat(res.getError(), is(nullValue()));			
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<Map> res = new JsonRpcResponse<Map>(message, Map.class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
		
}
