package com.worizon.junit.jsonrequest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.worizon.jsonrpc.JsonRpcRequest;
import com.worizon.jsonrpc.gson.NonExpose;

public class JsonRpcRequestTest {
		

	@Test
	public void testConstructor1() {
		
		Map<String, Object> params = new LinkedHashMap<String, Object>(); 
		JsonRpcRequest request = new JsonRpcRequest("test", params);
		assertNotNull( request.getId() );
		assertEquals( request.getVersion(), "2.0");
		assertEquals( request.getMethod(), "test");		
	}
	
	
	@Test
	public void testConstructor2() {
		
		
		JsonRpcRequest request = new JsonRpcRequest("test");
		assertNotNull( request.getId() );
		assertEquals( request.getVersion(), "2.0");
		assertEquals( request.getMethod(), "test");		
		assertTrue(request.toString().startsWith("{\"method\":\"test\",\"jsonrpc\":\"2.0\","));		
	}
	
		
	@Test
	public void testEquals(){
		
		JsonRpcRequest req1 = new JsonRpcRequest("test");		
		JsonRpcRequest req2 = new JsonRpcRequest("test");
		assertEquals( req1, req2 );
		
	}
		
	
	@Test
	public void testParamsOrderToString(){
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("y", new int[]{3,4,5});
		params.put("x", 1);
		params.put("z", true);
		JsonRpcRequest request = new JsonRpcRequest("test", params);
		request.setId(1000L);			
				
		assertEquals(request.toString(), "{\"method\":\"test\",\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":true},\"jsonrpc\":\"2.0\",\"id\":1000}");
				
	}
	
	
	public void testObjectToString(){
		
		class A{
			int x;
			String y;
			public A( int x, String y){
				
				this.x = x;
				this.y = y;
			}
		}
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("a", new A(5,"test"));
		JsonRpcRequest request = new JsonRpcRequest("test",params);
		request.setId(1000L);
		
		assertEquals(request.toString(), "{\"method\":\"test\",\"params\":{\"a\":{\"x\":5,\"y\":\"test\"}},\"jsonrpc\":\"2.0\",\"id\":1000}");
	}
	
	public void testObjectNonExposeFieldToString(){
		
		class A{
			int x;
			@NonExpose String y;
			public A( int x, String y){
				
				this.x = x;
				this.y = y;
			}
		}
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("a", new A(5,"test"));
		JsonRpcRequest request = new JsonRpcRequest("test");
		request.setId(1000L);
		
		assertEquals(request.toString(), "{\"method\":\"test\",\"params\":{\"a\":{\"x\":5}},\"jsonrpc\":\"2.0\",\"id\":1000}");
	}
	

}
