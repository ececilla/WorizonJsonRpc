package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcMapResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": {\"x\":1,\"y\":\"test\"}, \"id\": 2}";		
		JsonRpcResponse<Map> res = new JsonRpcResponse<Map>(message, Map.class);
		
		HashMap expected = new HashMap(){{
			put("x",1d);
			put("y","test");
		}};
		assertEquals(expected, res.getResult());			
		assertEquals( 2, res.getId().longValue() );				
		assertEquals("2.0",res.getVersion());
		assertNull(res.getError());			
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<Map> res = new JsonRpcResponse<Map>(message, Map.class);
		
		assertNull(res.getResult());
		
	}
		
}
