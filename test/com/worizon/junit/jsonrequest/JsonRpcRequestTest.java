package com.worizon.junit.jsonrequest;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
	public void testId(){
				
		for( int i=1; i < 10000; i++){
			
			JsonRpcRequest req = new JsonRpcRequest("test");
			assertTrue( req.getId().longValue() == i );
		}		
	}

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
	public void testConstructor3() {
		
		
		JsonRpcRequest request1 = new JsonRpcRequest("test");
		JsonRpcRequest request2 = new JsonRpcRequest( request1 );
		assertTrue( request1.equals(request2) );		
	}
	
		
	@Test
	public void testEquals(){
		
		JsonRpcRequest req1 = new JsonRpcRequest("test");		
		JsonRpcRequest req2 = new JsonRpcRequest("test");		
		assertFalse( req1.equals(req2) );
		
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
	
	class A{
		int x;
		String y;
		public A( int x, String y){
			
			this.x = x;
			this.y = y;
		}
	}	
	@Test
	public void testObjectToString(){
		
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("a", new A(5,"test"));
		JsonRpcRequest request = new JsonRpcRequest("test",params);
		request.setId(1000L);
		
		assertEquals("{\"method\":\"test\",\"params\":{\"a\":{\"x\":5,\"y\":\"test\"}},\"jsonrpc\":\"2.0\",\"id\":1000}", request.toString());
	}
	
	class B{
		int x;
		@NonExpose String y;
		public B( int x, String y){
			
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals( Object obj ){
			
			return ((B)obj).x == x && ((B)obj).y.equals(y); 
		}
	}
	@Test
	public void testObjectNonExposeFieldToString(){
						
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("b", new B(5,"test"));
		JsonRpcRequest request = new JsonRpcRequest("test", params);
		request.setId(1000L);
		
		assertEquals("{\"method\":\"test\",\"params\":{\"b\":{\"x\":5}},\"jsonrpc\":\"2.0\",\"id\":1000}", request.toString());
	}
	
	@Test
	public void testParseNumbers(){
		
		JsonRpcRequest req = JsonRpcRequest.parse("{\"method\":\"test_numbers\",\"params\":{\"x\":5,\"y\":10.0},\"jsonrpc\":\"2.0\",\"id\":1000}");
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("x", 5.0);
		params.put("y", 10.0);
		JsonRpcRequest expected = new JsonRpcRequest("test_numbers", params);
		expected.setId(1000L);
		assertEquals(expected, req);
	}
	
	@Test
	public void testParseStrings(){
		
		JsonRpcRequest req = JsonRpcRequest.parse("{\"method\":\"test_strings\",\"params\":{\"x\":\"foo\",\"y\":\"bar\"},\"jsonrpc\":\"2.0\",\"id\":1000}");
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("x", "foo");
		params.put("y", "bar");
		JsonRpcRequest expected = new JsonRpcRequest("test_strings", params);
		expected.setId(1000L);
		assertEquals(expected, req);
	}
	
	@Test
	public void testParseArray(){
		
		JsonRpcRequest req = JsonRpcRequest.parse("{\"method\":\"test_array\",\"params\":{\"x\":[1,2,3]},\"jsonrpc\":\"2.0\",\"id\":1000}");
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		ArrayList<Double> values = new ArrayList<Double>();
		values.add(1.0d);
		values.add(2.0d);
		values.add(3.0d);
		params.put("x", values);		
		JsonRpcRequest expected = new JsonRpcRequest("test_array", params);
		expected.setId(1000L);
		assertEquals(expected, req);
	}
		

}
