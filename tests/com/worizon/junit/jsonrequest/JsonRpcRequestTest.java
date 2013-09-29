package com.worizon.junit.jsonrequest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.worizon.jsonrpc.JsonRpcRequest;

public class JsonRpcRequestTest {

	
	
	@Before
	public void setUp() throws Exception {
				
	}

	@After
	public void tearDown() throws Exception {
				
	}

	@Test
	public void testConstructor() {
		
		JsonRpcRequest request = new JsonRpcRequest();
		assertNotNull( request.getId() );
		assertEquals( request.getVersion(), "2.0");
	}
	
	@Test
	public void testEquals(){
		
		JsonRpcRequest req1 = new JsonRpcRequest();
		JsonRpcRequest req2 = new JsonRpcRequest();
		assertEquals( req1.getId(), req2.getId() );
		
	}
	
	@Test
	public void testNonEquals() throws Exception{
		
		JsonRpcRequest req1 = new JsonRpcRequest();
		Thread.sleep(1);
		JsonRpcRequest req2 = new JsonRpcRequest();
		
		assert( !req1.getId().equals(req2.getId()) );
		
	}
	
	
	public void testJsonString(){
		
		//Gson gson = new Gson();		
		
	}

}
