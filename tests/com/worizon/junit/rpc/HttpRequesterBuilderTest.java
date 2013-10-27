package com.worizon.junit.rpc;

import org.junit.After;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.worizon.net.HttpRequester;
import com.worizon.net.HttpRequesterBuilder;

public class HttpRequesterBuilderTest {
	
	private TestServer server;
	private HttpRequester http;
	private HttpRequesterBuilder builder;
	
	@Before
	public void setUp() throws Exception{
		
		server = new TestServer(4444);		
		http = (HttpRequester)server.createTestRequester(new HttpRequester(), "request");
		builder = new HttpRequesterBuilder(http);
	}
		
		
	@Test
	public void testEndpoint() throws Exception{
						
		http = builder.endpoint("http://localhost:4444/rpc").build();
		server.finish();
		assertEquals("http://localhost:4444/rpc", http.getEndpoint());				
	}
	
	@Test
	public void testRequest() throws Exception{
				
		http = builder.endpoint("http://localhost:4444/rpc").build();
		http.request("test");
		assertEquals("test", server.getBody());
		assertEquals("application/json", server.getHeaders().get("Content-Type"));
		assertEquals("application/json", server.getHeaders().get("Accept"));		
		assertEquals("no-cache", server.getHeaders().get("Cache-Control"));
		assertEquals("no-cache", server.getHeaders().get("Pragma"));
		assertEquals("localhost:4444", server.getHeaders().get("Host"));
		assertEquals("close", server.getHeaders().get("Connection"));
		assertEquals("5", server.getHeaders().get("Content-Length"));
		
	}
	
	@Test
	public void testTransformers(){
		
		
	}

}
