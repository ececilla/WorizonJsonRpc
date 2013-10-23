package com.worizon.junit.rpc;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.worizon.net.HttpRequester;

public class HttpRequesterTest {
	
	private TestServer server;
	private TestServer.IRequester http;
	
	@Before
	public void setUp() throws Exception{
				
		server = new TestServer(4444);
		server.setAdapter(new TestServer.IRequester() {
			
			private HttpRequester myRequester = new HttpRequester();
			@Override
			public void setEndpoint(String endpoint) {
				
				myRequester.setEndpoint(endpoint);//my particular method to set the endpoint
			}
			
			@Override
			public void request(String body) throws Exception {
				
				myRequester.request(body);//my particular method to make the request.
			}
		});
		
		http = server.createTestRequester("http://localhost:4444/rpc");		
	}
	
	@Test
	public void testRequest() throws Exception{
		
						
		http.request("test");				
		assertEquals( "test", server.getBody() );
		assertEquals("application/json", server.getHeaders().get("Content-Type"));
		assertEquals("application/json", server.getHeaders().get("Accept"));		
		assertEquals("no-cache", server.getHeaders().get("Cache-Control"));
		assertEquals("no-cache", server.getHeaders().get("Pragma"));
		assertEquals("localhost:4444", server.getHeaders().get("Host"));
		assertEquals("keep-alive", server.getHeaders().get("Connection"));
		assertEquals("5", server.getHeaders().get("Content-Length"));
		server.dumpHeaders();
		
	}

}
