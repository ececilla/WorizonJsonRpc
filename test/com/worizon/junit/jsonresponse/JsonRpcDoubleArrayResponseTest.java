package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcDoubleArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1], \"id\": 2}";		
		JsonRpcResponse<double[]> res = new JsonRpcResponse<double[]>(message, double[].class);
	
		assertThat(  res.getResult(),is(equalTo(new double[]{-19.8d,10.2d,54.9d,67.1d})));		
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is("2.0"));
		assertThat(res.getError(), is(nullValue()));
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1,null], \"id\": 2}";		
		JsonRpcResponse<Double[]> res2 = new JsonRpcResponse<Double[]>(message2, Double[].class);
		assertThat(  res2.getResult(), is(equalTo(new Double[]{-19.8d,10.2d,54.9d,67.1d,null})) );		
		
	}
	
	@Test
	public void testNullResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<double[]> res = new JsonRpcResponse<double[]>(message, double[].class);
		assertThat(res.getResult(), is(nullValue()));
		
	}
		
}
