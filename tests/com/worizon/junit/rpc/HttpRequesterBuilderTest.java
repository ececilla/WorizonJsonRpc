package com.worizon.junit.rpc;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.worizon.net.HttpRequester;
import com.worizon.net.HttpRequesterBuilder;

public class HttpRequesterBuilderTest {
	
	/*
	private HttpRequesterBuilder builder;
	private String request;
	private HttpRequester requester;
	
	@Before
	public void setUp() throws Exception{
		
		requester = EasyMock.createNiceMock(HttpRequester.class);				
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				request = requestCapture.getValue();				
				return "";				
			}
		});
		EasyMock.replay(requester);
		builder = new HttpRequesterBuilder( requester );
	}
	
	@After
	public void tearDown(){
		
		builder = null;
		request = null;
		requester = null;
	}
	*/
	
	@Test
	public void testEndpoint() throws Exception{
		
		HttpRequesterBuilder builder = new HttpRequesterBuilder();
		HttpRequester requester = builder.endpoint("http://test.com/rpc").build();
		assertEquals("http://test.com/rpc", requester.getEndpoint());
				
	}
	
	@Test
	public void testPayloadConcat() throws Exception{
		
		
	}

}
