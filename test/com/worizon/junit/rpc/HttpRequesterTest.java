package com.worizon.junit.rpc;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

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
		
		assertThat(server.getBody(), is("test"));		
		assertThat(server.getHeaders().get("Content-Type"), is("application/json") );		
		assertThat(server.getHeaders().get("Cache-Control"), is("no-cache") );		
		assertThat(server.getHeaders().get("Pragma"), is("no-cache") );
		assertThat(server.getHeaders().get("Host"), is("localhost:4444") );
		assertThat(server.getHeaders().get("Connection"), is("close"));
		assertThat(server.getHeaders().get("Content-Length"), is("5"));		
		
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
		assertThat(req, is(notNullValue()));		
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
		
		assertThat(req, is(notNullValue()));
		assertThat(req.getMethod(), is("test") );
		assertThat(req.getVersion(), is("2.0") );
		assertThat(req.getId(), is(1000L));				
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
