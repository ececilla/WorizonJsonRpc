package com.worizon.junit.rpc;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.hamcrest.core.IsNot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcRequest;
import com.worizon.net.HttpRequest;
import com.worizon.net.HttpRequestBuilder;
import com.worizon.test.TestServer;

public class HttpRequestTest {
	
	private TestServer server;
	private TestServer.IRequester http;	
	private HttpRequest myRequest;
	
	@Before
	public void setUp() throws Exception{
		
		myRequest = new HttpRequest();
		server = new TestServer(4444);
		server.setAdapter(new TestServer.IRequester() {
									
			@Override
			public void setEndpoint(String endpoint) throws MalformedURLException{
				
				myRequest = new HttpRequestBuilder(myRequest).endpoint(endpoint).build();
				//myRequest.setEndpoint(endpoint);//my particular method to set the endpoint
			}
			
			@Override
			public void doRequest(String body) throws Exception {
				
				myRequest.perform(body);//my particular method to make the request.
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
		
		HttpRequest myRequester = new HttpRequest("httx://foobar");
	}
	
	@Test(expected=MalformedURLException.class)
	public void testWrongEndpointURLSyntax() throws MalformedURLException{
		
		HttpRequest myRequester = new HttpRequest("http://foobar.");
	}
	
	@Test
	public void testMissingEndpoint() throws Exception{
		
		HttpRequest requester = new HttpRequest();
		try{
			requester.perform("test body");
		}catch(IllegalStateException ise){
			
			assertThat(ise.getMessage(), is("Endpoint not set"));
		}
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
		assertThat(req.getParams(), is(notNullValue()));
		assertThat(req.getId(), is(1000L));				
	}
	
	@Test
	public void testRequestConnectionFailed() throws Exception{
		
		HttpRequestBuilder builder = new HttpRequestBuilder()
											.endpoint("http://localhost:5555")
											.requestRetries(1)
											.connectTimeout(1000);
		HttpRequest request = builder.build();
					
		try{
			request.perform("test");
			fail();
		}catch(ConnectException ce){
			
		}catch(SocketTimeoutException ste){
			
		}
	}
	
	@Test(expected=SocketTimeoutException.class)
	public void testRequestReadTimeout() throws Exception{
		
		server.setIdleTime(2000);
		HttpRequestBuilder builder = new HttpRequestBuilder()
										.endpoint("http://localhost:4444")
										.requestRetries(0)
										.readTimeout(1000);
		HttpRequest request = builder.build();				
		request.perform("test");			
	}		
					
	/*
	@Test
	public void testAddTransformersSynchronized() throws Exception{
		
		server.setIdleTime(2000);							
		Thread t = new Thread( new Runnable() {
			
			@Override
			public void run(){
				
				try{
					Thread.sleep(500);//secondary thread waits for main thread to get lock on requester object
					myRequest.addTransformers(new ArrayList<HttpRequest.ITransformer>());//synchronized
					assertThat(server.getBody(), is("bar"));	
				}catch(Exception ex){ ex.printStackTrace();}
			}
		});
		t.start();
		http.doRequest("bar");//main thread gets lock on requester object
									
	}
	*/

}
