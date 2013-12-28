package com.worizon.junit.rpc;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;


import com.worizon.jsonrpc.TransformerException;
import com.worizon.net.HttpRequest;
import com.worizon.net.HttpRequestBuilder;
import com.worizon.net.HttpRequest.TransformerContext;
import com.worizon.test.TestServer;

public class HttpRequestBuilderTest {
	
	private TestServer server;
	private HttpRequest request;
	private HttpRequestBuilder builder;
	
	@Before
	public void setUp() throws Exception{
		
		server = new TestServer(4444);		
		request = (HttpRequest)server.createTestRequester(new HttpRequest(), "perform");
		builder = new HttpRequestBuilder(request);
	}
	
	//finish server in case any test fails and server is not stopped implicitly
	@After
	public void tearDown() throws Exception{
		
		server.finish();
	}	
				
	@Test
	public void testEndpoint() throws Exception{
						
		request = builder.endpoint("http://localhost:4444/rpc").build();				
		assertThat(request.getEndpoint().toString(), is(equalTo("http://localhost:4444/rpc")));				
	}
	
	@Test
	public void testReadTimeout() throws Exception{
		
		request = builder.endpoint("http://localhost:4444/rpc").readTimeout(1000).build();
		assertThat(request.getReadTimeout(), is(1000));
	}
	
	@Test
	public void testConnectTimeout() throws Exception{
		
		request = builder.endpoint("http://localhost:4444/rpc").connectTimeout(1000).build();
		assertThat(request.getConnectTimeout(), is(1000));
	}
	
	@Test
	public void testRequestRetries() throws Exception{
		
		request = builder.endpoint("http://localhost:4444/rpc").requestRetries(5).build();
		assertThat(request.getRequestRetries(), is(5));
	}
		
	
	@Test
	public void testRequest() throws Exception{
				
		request = builder.endpoint("http://localhost:4444/rpc").build();		
		request.perform("test");
		
		assertThat(server.getBody(), is(equalTo("test")));
		assertThat(server.getHeaders().get("Content-Type"), is(equalTo("application/json")));
		assertThat(server.getHeaders().get("Accept"), is(equalTo("application/json")));		
		assertThat(server.getHeaders().get("Cache-Control"), is(equalTo("no-cache")));
		assertThat(server.getHeaders().get("Pragma"), is(equalTo("no-cache")));
		assertThat(server.getHeaders().get("Host"), is(equalTo("localhost:4444")));
		assertThat(server.getHeaders().get("Connection"), is(equalTo("close")));
		assertThat(server.getHeaders().get("Content-Length"), is(equalTo("5")));
		
	}
	
	@Test
	public void testAddTransformer() throws Exception{
		request = builder
				.endpoint("http://localhost:4444/rpc2")				
				.addTransformer(new HttpRequest.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx)  {						
						
						ctx.setBody("foo\n");
					}
				}).build();		
		assertThat(request.getEndpoint().toString(),is("http://localhost:4444/rpc2"));
		
		request.perform("bar");
		
		assertThat(server.getBody(), is(equalTo("foo")));		
	}		
	
	@Test
	public void testAddTransformerWithException() throws Throwable{
		request = builder
				.endpoint("http://localhost:4444/rpc")				
				.addTransformer(new HttpRequest.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) throws Throwable{						
						
						throw new IllegalStateException("Illegal state to apply this transformer");
					}
				}).build();		
		try{
			request.perform("bar");
		}catch(TransformerException re){
						
			assertThat( re.getCause() instanceof IllegalStateException, is(true) );
			assertThat(re.getCause().getMessage(), is("Illegal state to apply this transformer"));
		}
		
	}
	
	@Test
	public void testConcatTransformers() throws Exception{
		
		request = builder
				.endpoint("http://localhost:4444/rpc")				
				.addTransformer(new HttpRequest.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx){						
						
						ctx.setBody("foo\n");
					}
				})
				.addTransformer(new HttpRequest.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) {
							
						assertThat(ctx.getBody(),is(equalTo("foo\n")) );
						ctx.putHeader("Content-Type", "text/xml");
					}
				})
				.build();		
		request.perform("bar");
		
		assertThat(server.getBody(), is("foo"));
		assertThat(server.getHeaders().get("Content-Type"), is("text/xml") );
		
	}
	
	@Test
	public void testSkipNextIfTrueTransformer() throws Exception{
		
		request = builder
				.endpoint("http://localhost:4444/rpc")								
				.skipNextIfTrue(true)
				.addTransformer(new HttpRequest.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) {
							
						throw new RuntimeException();					
					}
				}).build();		
		request.perform("bar");
		
		assertThat(server.getBody(), is("bar"));
		assertThat(server.getHeaders().get("Content-Type"), is("application/json"));
		
	}
	
	@Test
	public void testContinueIfTrueTransformer() throws Exception{
		
		request = builder
				.endpoint("http://localhost:4444/rpc")								
				.continueIfTrue(false)
				.addTransformer(new HttpRequest.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx){
							
						fail();						
					}
				}).build();		
		request.perform("bar");
		
		assertThat(server.getBody(), is(equalTo("bar")));		
		
	}	
	
	@Test
	public void testBodyConcatTransformer() throws Exception{
		
		request = builder
				.endpoint("http://localhost:4444/rpc")	
				.addTransformer(new HttpRequest.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx) {
						
						ctx.setBody( ctx.getBody().trim() );
					}
				})
				.bodyConcat("test2\n")
				.build();		
		request.perform("bar");
		
		assertThat(server.getBody(), is("bartest2"));		
	}
		
	@Test
	public void testBodyPreprendTransformer() throws Exception{
		
		request = builder
				.endpoint("http://localhost:4444/rpc")					
				.bodyPrepend("foo")
				.build();		
		request.perform("bar");
		
		assertThat(server.getBody(), is("foobar") );		
	}
	
	@Test
	public void testBodyURLEncodeTransformer() throws Exception{
		
		request = builder
				.endpoint("http://localhost:4444/rpc")					
				.bodyTrim()
				.bodyURLEncode()
				.bodyConcat("\n")
				.build();		
		request.perform("{test:1}");	
		
		assertThat(server.getBody().toCharArray(), 
					is(new char[]{'%','7','B','t','e','s','t','%','3','A','1','%','7','D'})
					);		
	}
			
}
