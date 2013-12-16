package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcException;
import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcErrorResponseTest {
	

	@Test
	public void testErrorWithCodeWithMessageWithData() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":\"test error\",\"data\":\"foo\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertThat( res.getError().getCode(), is(-100) );
		assertThat(res.getError().getMessage(), is(equalTo("test error")));	
		assertThat((String)res.getError().getData(), is(equalTo("foo")));
		assertThat( res.getError().toString(), is(equalTo("{code:-100, message:'test error', data:foo}")) );
		assertThat(res.getResult(), is(nullValue()));		
	}
	
	@Test
	public void testErrorWithCodeWithMessage() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":\"test error\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertThat( res.getError().getCode(), is(-100) );
		assertThat(res.getError().getMessage(), is(equalTo("test error")));
		assertThat(res.getError().toString(), is(equalTo("{code:-100, message:'test error'}")) );
		assertThat(res.getResult(), is(nullValue()));		
	}
	
	@Test
	public void testIsCustomErrorTrue() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":\"test error\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertThat(res.getError().getCode(), is(-100) );
		assertThat(res.getError().getMessage(), is(equalTo("test error")));
		assertThat(res.getError().toString(), is(equalTo("{code:-100, message:'test error'}")) );
		assertThat(res.getError().isCustomError(), is(true));
		assertThat(res.getResult(),is(nullValue()));		
	}
	
	@Test
	public void testIsCustomErrorFalse() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-32601, \"message\":\"method not found\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertThat( res.getError().getCode(), is(-32601) );
		assertThat( res.getError().getMessage(), is(equalTo("method not found")));
		assertThat(res.getError().toString(), is(equalTo("{code:-32601, message:'method not found'}")) );
		assertThat(res.getError().isCustomError(), is(false));
		assertThat(res.getResult(), is(nullValue()));		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorAndResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {}, \"result\":{}, \"id\": 2}";		
		JsonRpcResponse<Void> res = new JsonRpcResponse<Void>(message, Void.class);												
	}
	
		
	@Test(expected=JsonRpcException.class)
	public void testErrorWithMessageNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100, \"message\":null}, \"id\": 2}";		
		JsonRpcResponse<Integer> res = new JsonRpcResponse<Integer>(message, Integer.class);
		assertThat( res.getError().getCode(), is(-100) );
		assertThat(res.getError().getMessage(), is(nullValue()));				
		assertThat(res.getError().toString(), is(equalTo("{code:-100, message:'null'}")) );
		assertThat(res.getResult(), is(nullValue()));		
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorWithCodeNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":null, \"message\":\"Not found\"}, \"id\": 2}";		
		new JsonRpcResponse<Integer>(message, Integer.class);
				
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorWithMessageNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-100}, \"id\": 2}";		
		new JsonRpcResponse<Integer>(message, Integer.class);
				
	}
	
	@Test(expected=JsonRpcException.class)
	public void testErrorWithCodeNotFound() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"error\": {\"message\":null}, \"id\": 2}";		
		new JsonRpcResponse<Void>(message, Void.class);
		
	}		
	
}
