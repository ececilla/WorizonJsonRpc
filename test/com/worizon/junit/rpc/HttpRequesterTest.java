package com.worizon.junit.rpc;

import static org.junit.Assert.*;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcRequest;
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
			public void setEndpoint(String endpoint) throws MalformedURLException{
				
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
	
	@Test(expected=MalformedURLException.class)
	public void testWrongEndpointProtocol() throws MalformedURLException{
		
		HttpRequester myRequester = new HttpRequester("httx://foobar");
	}
	
	@Test(expected=MalformedURLException.class)
	public void testWrongEndpointURLSyntax() throws MalformedURLException{
		
		HttpRequester myRequester = new HttpRequester("http://foobar.");
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseVersionMissing() throws Exception{
		
		JsonRpcRequest req = JsonRpcRequest.parse("{\"method\":\"test\",\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":true},\"id\":1000}");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseMethodMissing() throws Exception{
		
		JsonRpcRequest req = JsonRpcRequest.parse("{\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":true},\"jsonrpc\":\"2.0\",\"id\":1000}");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseIdMissing() throws Exception{
		
		JsonRpcRequest req = JsonRpcRequest.parse("{\"method\":\"test\",\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":true},\"jsonrpc\":\"2.0\"}");
	}
	
	@Test
	public void testParseOK() throws Exception{
		
		JsonRpcRequest req = JsonRpcRequest.parse("{\"method\":\"test\",\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":true},\"jsonrpc\":\"2.0\",\"id\":1000}");
		assertNotNull(req);
	}
	
	@Test
	public void testRequestJson() throws Exception{
		
		server.setBodyMapper(new TestServer.IMapper() {
			
			@Override
			public Object map(String str) {
				
				return JsonRpcRequest.parse(str);
			}
		});
		http.doRequest("{\"method\":\"test\",\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":true},\"jsonrpc\":\"2.0\",\"id\":1000}");						
		JsonRpcRequest req = (JsonRpcRequest)server.getBodyAsObject();
		assertNotNull(req);
		assertEquals("test", req.getMethod());
		assertEquals("2.0",req.getVersion());
		assertEquals(1000d, req.getId().longValue(),0.001);		
		
	}
	
	@Test
	public void testRequestConnectionFailed() throws Exception{
		
		HttpRequester requester = new HttpRequester("http://localhost:5555");
		requester.setRequestRetries(1);
		requester.setConnectTimeout(1000);		
		try{
			requester.request("test");
			fail();
		}catch(ConnectException ce){
			
		}catch(SocketTimeoutException ste){
			
		}
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
