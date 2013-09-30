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
import com.worizon.jsonrpc.NonExpose;

public class JsonRpcRequestTest {
		

	@Test
	public void testConstructor1() {
		
		JsonRpcRequest request = new JsonRpcRequest("test");
		assertNotNull( request.getId() );
		assertEquals( request.getVersion(), "2.0");
		assertEquals( request.getMethod(), "test");
		assertNotNull( request.getParams());
	}
	
	@Test
	public void testConstructor2(){
		
		class RpcProxy{
			
			void call(){
				
				JsonRpcRequest req = new JsonRpcRequest();
				assertEquals("remoteOperation", req.getMethod());
			}
		};
		
		class Service{
			
			void remoteOperation(){
				
				new RpcProxy().call();
			}
		}
		new Service().remoteOperation();
		
		
		
	}
	
		
	@Test
	public void testEquals(){
		
		JsonRpcRequest req1 = new JsonRpcRequest("test");		
		JsonRpcRequest req2 = new JsonRpcRequest("test");
		assertEquals( req1, req2 );
		
	}
		
	
	@Test
	public void testParamsOrderToString(){
				
		JsonRpcRequest request = new JsonRpcRequest("test");
		request.setId(1000L);
		
		request.addParam("y", new int[]{3,4,5});
		request.addParam("x", 1);		
		request.addParam("z", true);
				
		assertEquals(request.toString(), "{\"method\":\"test\",\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":1},\"jsonrpc\":\"2.0\",\"id\":1000}");
				
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
		JsonRpcRequest request = new JsonRpcRequest("test");
		request.setId(1000L);
		request.addParam("a", new A(5,"test"));
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
		JsonRpcRequest request = new JsonRpcRequest("test");
		request.setId(1000L);
		request.addParam("a", new A(5,"test"));
		assertEquals(request.toString(), "{\"method\":\"test\",\"params\":{\"a\":{\"x\":5}},\"jsonrpc\":\"2.0\",\"id\":1000}");
	}
	
	
	
	@Test
	public void testAddParam(){
		
		Map<String, Object > params = new LinkedHashMap<String, Object>();
		JsonRpcRequest request = new JsonRpcRequest("test", params);
		request.addParam("test-key", 1);		
		assertEquals( params.get("test-key"),1);
	}

}
