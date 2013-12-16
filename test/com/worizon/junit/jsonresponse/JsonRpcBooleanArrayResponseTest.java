package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcBooleanArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [1,1,false,true,0], \"id\": 2}";		
		JsonRpcResponse<boolean[]> res = new JsonRpcResponse<boolean[]>(message, boolean[].class);
				
		assertThat(Arrays.equals(new boolean[]{true,true,false,true,false}, res.getResult()), is(true));
		assertThat(res.getId(), is(equalTo(2L)) );
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));		
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [1,1,false,true,0,null], \"id\": 2}";		
		JsonRpcResponse<Boolean[]> res2 = new JsonRpcResponse<Boolean[]>(message2, Boolean[].class);
		assertThat(Arrays.equals(new Boolean[]{true,true,false,true,false,null}, res2.getResult()), is(true));
		
		
	}
	
	@Test
	public void testNullResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<boolean[]> res = new JsonRpcResponse<boolean[]>(message, boolean[].class);
		assertThat(res.getResult(), is(nullValue()));		
		
	}
		
}
