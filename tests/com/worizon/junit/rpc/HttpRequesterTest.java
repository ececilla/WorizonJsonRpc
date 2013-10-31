package com.worizon.junit.rpc;

import static org.junit.Assert.*;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.worizon.net.HttpRequester;
import com.worizon.test.TestServer;

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
			public void doRequest(String body) throws Exception {
				
				myRequester.request(body);//my particular method to make the request.
			}
		});
		
		http = server.createTestRequester("http://localhost:4444/rpc");		
	}
	
	@After
	public void tearDown() throws Exception{
		
		server.finish();
	}
	
	@Test
	public void testRequest() throws Exception{
		
						
		http.doRequest("test");				
		assertEquals( "test", server.getBody() );
		assertEquals("application/json", server.getHeaders().get("Content-Type"));
		assertEquals("application/json", server.getHeaders().get("Accept"));		
		assertEquals("no-cache", server.getHeaders().get("Cache-Control"));
		assertEquals("no-cache", server.getHeaders().get("Pragma"));
		assertEquals("localhost:4444", server.getHeaders().get("Host"));
		assertEquals("close", server.getHeaders().get("Connection"));
		assertEquals("5", server.getHeaders().get("Content-Length"));		
		
	}
	
	@Test(expected=ConnectException.class)
	public void testRequestConnectionRefused() throws Exception{
		
		HttpRequester requester = new HttpRequester("http://localhost:5555");
		requester.setRequestRetries(1);
		requester.setConnectTimeout(1000);
		requester.request("test");
	}
	
	@Test(expected=SocketTimeoutException.class)
	public void testRequestReadTimeout() throws Exception{
		
		server.setIdleTime(2000);
		HttpRequester requester = new HttpRequester("http://localhost:4444");
		requester.setRequestRetries(0);
		requester.setReadTimeout(1000);
		requester.request("test");			
	}
	
	

}
