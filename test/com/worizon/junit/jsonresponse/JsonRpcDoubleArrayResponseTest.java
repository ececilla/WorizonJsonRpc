package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcDoubleArrayResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1], \"id\": 2}";		
		JsonRpcResponse<double[]> res = new JsonRpcResponse<double[]>(message, double[].class);
	
		assertArrayEquals( new double[]{-19.8d,10.2d,54.9d,67.1d}, res.getResult(),0);		
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": [-19.8,10.2,54.9,67.1,null], \"id\": 2}";		
		JsonRpcResponse<Double[]> res2 = new JsonRpcResponse<Double[]>(message2, Double[].class);
		assertArrayEquals( new Double[]{-19.8d,10.2d,54.9d,67.1d,null}, res2.getResult());		
		
	}
	
	@Test
	public void testNullResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<double[]> res = new JsonRpcResponse<double[]>(message, double[].class);
		assertNull(res.getResult());
		
	}
		
}
