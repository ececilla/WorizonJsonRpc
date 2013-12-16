package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.google.gson.JsonParseException;
import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcBooleanResponseTest {
	

	@Test
	public void testResultTrue() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": true, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertThat(res.getResult(), is(true));
		assertThat(res.getId(), is(equalTo(2L)));
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(),is(nullValue()));		
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertThat( res2.getResult(), is(true) );
		
	}
	
	@Test
	public void testResult1() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 1, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertThat( res.getResult(), is(true) );
		assertThat(res.getId(), is(equalTo(2L)));
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));		
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertThat( res2.getResult(), is(true) );
		
	}
	
	@Test
	public void testResultFalse() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": false, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertThat( res.getResult(), is(false) );	
		assertThat( res.getId(), is(equalTo(2L)) );				
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertThat(res2.getResult(), is(false) );
		
	}
	
	@Test
	public void testResult0() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 0, \"id\": 2}";		
		JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
						
		assertThat( res.getResult(), is(false) );	
		assertThat( res.getId(), is(equalTo(2L)) );				
		assertThat( res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		JsonRpcResponse<Boolean> res2 = new JsonRpcResponse<Boolean>(message, boolean.class);
		assertThat( res2.getResult(), is(false) );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<String> res = new JsonRpcResponse<String>(message, String.class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
	
	@Test
	public void testParseException1() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": \"test\", \"id\": 2}";
		try{
			JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
		}catch(JsonParseException jpe){
			
			assertThat(jpe.getMessage(), is(equalTo("Boolean value \"test\" not valid")));
		}
	}
	
	@Test
	public void testParseException2() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [1,2,3], \"id\": 2}";
		try{
			JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
		}catch(JsonParseException jpe){
			
			assertThat(jpe.getMessage(), is(equalTo("Boolean value [1,2,3] not valid")));
		}
	}
	
	@Test
	public void testParseException3() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": {}, \"id\": 2}";
		try{
			JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
		}catch(JsonParseException jpe){
			
			assertThat(jpe.getMessage(), is(equalTo("Boolean value {} not valid")));
		}
	}
	
	@Test
	public void testParseException4() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 4.5, \"id\": 2}";
		try{
			JsonRpcResponse<Boolean> res = new JsonRpcResponse<Boolean>(message, Boolean.class);
		}catch(JsonParseException jpe){
			
			assertThat(jpe.getMessage(), is(equalTo("Boolean value 4.5 not valid")));
		}
	}
	
	
}
