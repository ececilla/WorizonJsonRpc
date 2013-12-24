package com.worizon.junit.rpc;

import java.util.ArrayList;
import java.util.List;

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
		assertThat(http.getEndpoint().toString(), is(equalTo("http://localhost:4444/rpc")));				
	}
		
	
	@Test
	public void testRequest() throws Exception{
				
		http = builder.endpoint("http://localhost:4444/rpc").build();		
		http.request("test");
		
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
		http = builder
				.endpoint("http://localhost:4444/rpc2")				
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx)  {						
						
						ctx.setBody("foo\n");
					}
				}).build();		
		assertThat(http.getEndpoint().toString(),is("http://localhost:4444/rpc2"));
		
		http.request("bar");
		
		assertThat(server.getBody(), is(equalTo("foo")));		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAddPreviousTransformer() throws Exception{
		List<HttpRequester.ITransformer> transformers = new ArrayList<HttpRequester.ITransformer>();
		transformers.add(new HttpRequester.ITransformer() {
			
			@Override
			public void transform(TransformerContext ctx) throws Throwable {				
				//do nothing
			}
		});
		http.addTransformers( transformers );
		http = builder
				.endpoint("http://localhost:4444/rpc2")				
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx)  {						
						
						ctx.setBody("foo\n");
					}
				}).build();		
											
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
			assertThat(re.getCause().getMessage(), is("Illegal state to apply this transformer"));
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
							
						assertThat(ctx.getBody(),is(equalTo("foo\n")) );
						ctx.putHeader("Content-Type", "text/xml");
					}
				})
				.build();		
		http.request("bar");
		
		assertThat(server.getBody(), is("foo"));
		assertThat(server.getHeaders().get("Content-Type"), is("text/xml") );
		
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
		
		assertThat(server.getBody(), is("bar"));
		assertThat(server.getHeaders().get("Content-Type"), is("application/json"));
		
	}
	
	@Test
	public void testContinueIfTrueTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")								
				.continueIfTrue(false)
				.addTransformer(new HttpRequester.ITransformer() {
					
					@Override
					public void transform(TransformerContext ctx){
							
						fail();						
					}
				}).build();		
		http.request("bar");
		
		assertThat(server.getBody(), is(equalTo("bar")));		
		
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
		
		assertThat(server.getBody(), is("bartest2"));		
	}
		
	@Test
	public void testBodyPreprendTransformer() throws Exception{
		
		http = builder
				.endpoint("http://localhost:4444/rpc")					
				.bodyPrepend("foo")
				.build();		
		http.request("bar");
		
		assertThat(server.getBody(), is("foobar") );		
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
		
		assertThat(server.getBody().toCharArray(), 
					is(new char[]{'%','7','B','t','e','s','t','%','3','A','1','%','7','D'})
					);		
	}
			
}
