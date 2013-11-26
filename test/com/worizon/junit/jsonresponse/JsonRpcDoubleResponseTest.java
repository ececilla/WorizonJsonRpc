package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcDoubleResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 348739478.2749223234, \"id\": 2}";		
		JsonRpcResponse<Double> res = new JsonRpcResponse<Double>(message, Double.class);
				
		assertEquals( (Double)348739478.2749223234d, res.getResult() );
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());
		
		JsonRpcResponse<Double> res2 = new JsonRpcResponse<Double>(message, double.class);
		assertEquals( (Double)348739478.2749223234d, res2.getResult() );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		
		JsonRpcResponse<Double> res = new JsonRpcResponse<Double>(message, Double.class);		
		assertNull(res.getResult());
	}
	
}
