package com.worizon.junit.rpc;

import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;


import com.worizon.jsonrpc.TransformerException;
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
		assertThat("http://localhost:4444/rpc", is(equalTo(http.getEndpoint().toString())));				
	}
		
	
	@Test
	public void testRequest() throws Exception{
				
		http = builder.endpoint("http://localhost:4444/rpc").build();		
		http.request("test");
		
		assertThat("test", is(equalTo(server.getBody())));
		assertThat("application/json", is(equalTo(server.getHeaders().get("Content-Type"))));
		assertThat("application/json", is(equalTo(server.getHeaders().get("Accept"))));		
		assertThat("no-cache", is(equalTo(server.getHeaders().get("Cache-Control"))));
		assertThat("no-cache", is(equalTo(server.getHeaders().get("Pragma"))));
		assertThat("localhost:4444", is(equalTo(server.getHeaders().get("Host"))));
		assertThat("close", is(equalTo(server.getHeaders().get("Connection"))));
		assertThat("5", is(equalTo(server.getHeaders().get("Content-Length"))));
		
	}
	
	@Test
	public void testAddTransformer() throws Exception{
		http = builder
				.endpoint("http://localhost:4444/rpc")				
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx)  {						
						
						ctx.setBody("foo\n");
					}
				}).build();		
		http.request("bar");
		
		assertThat("foo", is(equalTo(server.getBody())));
		
	}
	
	@Test
	public void testAddTransformerWithException() throws Throwable{
		http = builder
				.endpoint("http://localhost:4444/rpc")				
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) throws Throwable{						
						
						throw new IllegalStateException("Illegal state to apply this transformer");
					}
				}).build();		
		try{
			http.request("bar");
		}catch(TransformerException re){
						
			assertThat( re.getCause() instanceof IllegalStateException, is(true) );
		}
		
	}
	
	@Test
	public void testConcatTransformers() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")				
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx){						
						
						ctx.setBody("foo\n");
					}
				})
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) {
							
						assertThat("foo\n",is(equalTo(ctx.getBody())) );
						ctx.putHeader("Content-type", "text/xml");
					}
				})
				.build();		
		http.request("bar");
		
		assertThat("foo", is(equalTo(server.getBody())));
		assertThat("text/xml", is(equalTo(server.getHeaders().get("Content-Type"))) );
		
	}
	
	@Test
	public void testSkipNextIfTrueTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")								
				.skipNextIfTrue(true)
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) {
							
						throw new RuntimeException();					
					}
				}).build();		
		http.request("bar");
		
		assertThat("bar", is(equalTo(server.getBody())));
		assertThat("application/json", is(equalTo(server.getHeaders().get("Content-Type"))));
		
	}
	
	@Test
	public void testContinueIfTrueTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")								
				.continueIfTrue(false)
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx){
							
						assertTrue(false);						
					}
				}).build();		
		http.request("bar");
		
		assertThat("bar", is(equalTo(server.getBody())));		
		
	}	
	
	@Test
	public void testBodyConcatTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")	
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) {
						
						ctx.setBody( ctx.getBody().trim() );
					}
				})
				.bodyConcat("test2\n")
				.build();		
		http.request("bar");
		
		assertThat("bartest2", is(equalTo(server.getBody())));		
	}
		
	@Test
	public void testBodyPreprendTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")					
				.bodyPrepend("test2")
				.build();		
		http.request("bar");
		
		assertThat("test2bar", is(equalTo(server.getBody())) );		
	}
	
	@Test
	public void testBodyURLEncodeTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")					
				.bodyTrim()
				.bodyURLEncode()
				.bodyConcat("\n")
				.build();		
		http.request("{test:1}");	
		
		assertThat(new char[]{'%','7','B','t','e','s','t','%','3','A','1','%','7','D'}, 
					is(equalTo(server.getBody().toCharArray()))
					);		
	}
			
}
