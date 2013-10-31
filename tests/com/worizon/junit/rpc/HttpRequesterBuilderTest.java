package com.worizon.junit.rpc;

import java.io.IOException;

import org.junit.After;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.worizon.net.HttpRequester;
import com.worizon.net.HttpRequesterBuilder;
import com.worizon.net.HttpRequester.TransformerContext;
import com.worizon.test.TestServer;

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
	
	//finish server in case any test fails and server is not stopped implicitly
	@After
	public void tearDown() throws Exception{
		
		server.finish();
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
	public void testAddTransformer() throws Exception{
		http = builder
				.endpoint("http://localhost:4444/rpc")				
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) throws Exception {						
						
						ctx.setBody("foo\n");
					}
				}).build();		
		http.request("bar");
		assertEquals("foo", server.getBody());
		
	}
	
	@Test
	public void testConcatTransformers() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")				
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) throws Exception {						
						
						ctx.setBody("foo\n");
					}
				})
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) throws Exception {
							
						assertEquals("foo\n",ctx.getBody());
						ctx.putHeader("Content-type", "text/xml");
					}
				})
				.build();		
		http.request("bar");
		assertEquals("foo", server.getBody());
		assertEquals("text/xml", server.getHeaders().get("Content-Type"));
		
	}
	
	@Test
	public void testSkipNextIfTrueTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")								
				.skipNextIfTrue(true)
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) throws Exception {
							
						throw new RuntimeException();					
					}
				}).build();		
		http.request("bar");
		assertEquals("bar", server.getBody());
		assertEquals("application/json", server.getHeaders().get("Content-Type"));
		
	}
	
	@Test
	public void testContinueIfTrueTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")								
				.continueIfTrue(false)
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) throws Exception {
							
						assertTrue(false);						
					}
				}).build();		
		http.request("bar");
		assertEquals("bar", server.getBody());
		assertEquals("application/json", server.getHeaders().get("Content-Type"));
		
	}

}
