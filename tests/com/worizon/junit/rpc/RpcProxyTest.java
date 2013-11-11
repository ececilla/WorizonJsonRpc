package com.worizon.junit.rpc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcException;
import com.worizon.jsonrpc.RemoteException;
import com.worizon.jsonrpc.RpcProxy;
import com.worizon.jsonrpc.annotations.LocalException;
import com.worizon.jsonrpc.annotations.LocalExceptions;
import com.worizon.jsonrpc.annotations.Remote;
import com.worizon.jsonrpc.annotations.RemoteParams;
import com.worizon.jsonrpc.annotations.RemoteProcName;
import com.worizon.net.HttpRequester;


public class RpcProxyTest {
	
	interface NonRemoteInterface{};
	
	@Test(expected = IllegalArgumentException.class)	
	public void testNonRemoteInterface(){
		
		HttpRequester http = new HttpRequester("http://localhost:8080/rpc");
		RpcProxy proxy = new RpcProxy(http);
		NonRemoteInterface remote = proxy.create(NonRemoteInterface.class);
		
	}
	
	@Remote
	interface My1RemoteInterface{
		
		public Void test();
	};
	
	@Test	
	public void testNonAnotattedParams() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();				
				assertTrue( request.startsWith("{\"method\":\"test\",\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\":{} , \"id\": 2}";
				
			}
		});
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		My1RemoteInterface remote = proxy.create(My1RemoteInterface.class);
		remote.test();
	}
	
	@Remote
	interface My2RemoteInterface{
		
		@RemoteParams({"x","y","z"})
		public void test(int x, int y);
	};
	
	@Test(expected=IllegalArgumentException.class)
	public void testAnnottedParamsNumberMismath() throws Exception{
		
		HttpRequester requester = new HttpRequester();										
		RpcProxy proxy = new RpcProxy(requester);
		My2RemoteInterface remote = proxy.create(My2RemoteInterface.class);
		remote.test(1,2);
	}
	
	@Remote
	interface My3RemoteInterface{
		
		@RemoteParams({"params"})
		public Void test( Map<String, Object> params );
	}
		
	@Test
	public void testRemoteInterfaceWithHashMapParamAnnotatedWithParamsName() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();					
				assertTrue( request.startsWith("{\"method\":\"test\",\"params\":{\"x\":1,\"y\":\"test string\"},\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\":{} , \"id\": 2}";
				
			}
		});
		
		EasyMock.replay(requester);
		RpcProxy proxy = new RpcProxy(requester);
		My3RemoteInterface remote = proxy.create( My3RemoteInterface.class);
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("x", 1);
		params.put("y","test string");
		remote.test(params);
		
	}
	
	@Remote
	interface My4RemoteInterface{
		
		@RemoteParams({"params"})
		public Void test( List<Object> params );
	}
		
	@Test
	public void testRemoteInterfaceWithListParamAnnotatedWithParamsName() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();					
				assertTrue( request.startsWith("{\"method\":\"test\",\"params\":[1,\"test string\"],\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\":{} , \"id\": 2}";
				
			}
		});
		
		EasyMock.replay(requester);
		RpcProxy proxy = new RpcProxy(requester);
		My4RemoteInterface remote = proxy.create( My4RemoteInterface.class);
		List<Object> params = new ArrayList<Object>();
		params.add(1);
		params.add("test string");
		remote.test(params);
		
	}
	
	
	@Remote
	interface My5RemoteInterface{
		
		@RemoteParams({"x","y"})		
		public int sum( int x, int y);
	}
	
	@Test
	public void testRemoteInterfaceWithNamedParameters() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();		
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();					
				assertTrue( request.startsWith("{\"method\":\"sum\",\"params\":{\"x\":5,\"y\":4},\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\":9 , \"id\": 2}";
				
			}
		});
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		My5RemoteInterface remote = proxy.create(My5RemoteInterface.class);
				
		assertEquals(9, remote.sum(5, 4));
	}
	
	@Remote
	interface My6RemoteInterface{
					
		public int sum( int x, int y);
	}
	
	@Test
	public void testRemoteInterfaceWithNumberedParameters() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();		
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();					
				assertTrue( request.startsWith("{\"method\":\"sum\",\"params\":[5,4],\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\":9 , \"id\": 2}";
				
			}
		});
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		My6RemoteInterface remote = proxy.create(My6RemoteInterface.class);
				
		assertEquals(9, remote.sum(5, 4));
	}	
	
	
	
	class A{
		int x;
		int y;
		public A(int x, int y){this.x = x; this.y = y;}
	}
	class B{
		String z;
		float f;
		public B(String z, float f){this.z = z; this.f = f;}
		
		public boolean equals( Object obj ){
			B b = (B)obj;
			return b.z.equals(z) && b.f == b.f;
		}
	}
	@Remote
	interface My7RemoteInterface{
				
		@RemoteParams({"a"})		
		public B op( A a );// A object -> Remote operation op -> B object
		
	}
	
	@Test
	public void testRemoteInterfaceWithNamedObjectParameters() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();					
				assertTrue( request.startsWith("{\"method\":\"op\",\"params\":{\"a\":{\"x\":2,\"y\":3}},\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\": {\"z\":\"test\",\"f\":23.45}, \"id\": 2}";
				
			}
		});		
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		My7RemoteInterface remote = proxy.create(My7RemoteInterface.class);
		
		A a = new A(2,3);
		B expected = new B("test",23.45f);
		assertEquals(expected, remote.op(a));
	}
	
	@Remote
	interface My8RemoteInterface{
							
		public B op( A a );// A object -> Remote operation op -> B object
		
	}
	
	@Test
	public void testRemoteInterfaceWithNnumberedObjectParameters() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();					
				assertTrue( request.startsWith("{\"method\":\"op\",\"params\":[{\"x\":2,\"y\":3}],\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\": {\"z\":\"test\",\"f\":23.45}, \"id\": 2}";
				
			}
		});		
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		My8RemoteInterface remote = proxy.create(My8RemoteInterface.class);
		
		A a = new A(2,3);
		B expected = new B("test",23.45f);
		assertEquals(expected, remote.op(a));
	}	
	
	@Remote
	interface My9RemoteInterface{
		
		@RemoteProcName("dummy")
		public Void op();
		
	}
	
	@Test
	public void testRemoteInterfaceProcName() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();				
				assertTrue( request.startsWith("{\"method\":\"dummy\",\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";
				
			}
		});		
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		My9RemoteInterface remote = proxy.create(My9RemoteInterface.class);
		remote.op();						
	}	
	
	@Remote
	interface My10RemoteInterface{
							
		public Void op();// A object -> Remote operation op -> B object
		
	}
	
	@Test
	public void testJsonRpcExceptionParseError() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-32700,\"message\":\"Parse error\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My10RemoteInterface.class).op();
			assertTrue(false);
		}catch(JsonRpcException ex){
			
			assertEquals(-32700,ex.getCode());
			assertEquals("Parse error",ex.getMessage());
		}
	}	
	
	@Test
	public void testJsonRpcExceptionInvalidRequest() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-32600,\"message\":\"Invalid request\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My10RemoteInterface.class).op();
			assertTrue(false);
		}catch(JsonRpcException ex){
			
			assertEquals(-32600,ex.getCode());
			assertEquals("Invalid request",ex.getMessage());
		}
	}
	
	@Test
	public void testJsonRpcExceptionMethodNotFound() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-32601,\"message\":\"Method not found\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My10RemoteInterface.class).op();
			assertTrue(false);
		}catch(JsonRpcException ex){
			
			assertEquals(-32601,ex.getCode());
			assertEquals("Method not found",ex.getMessage());
		}
	}
	
	@Test
	public void testJsonRpcExceptionInvalidParams() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-32602,\"message\":\"Invalid params\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My10RemoteInterface.class).op();
			assertTrue(false);
		}catch(JsonRpcException ex){
			
			assertEquals(-32602,ex.getCode());
			assertEquals("Invalid params",ex.getMessage());
		}
	}
	
	@Test
	public void testJsonRpcExceptionInternalError() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-32603,\"message\":\"Internal error\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My10RemoteInterface.class).op();
			assertTrue(false);
		}catch(JsonRpcException ex){
			
			assertEquals(-32603,ex.getCode());
			assertEquals("Internal error",ex.getMessage());
		}
	}
	
	@Test
	public void testRemoteException() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-5,\"message\":\"Domain error\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My10RemoteInterface.class).op();
			assertTrue(false);
		}catch(RemoteException ex){
			
			assertEquals(-5,ex.getCode());
			assertEquals("Domain error",ex.getMessage());
		}
	}
	
	public static class MyDummyException extends RuntimeException{
		
		public MyDummyException(){
			super();
		}
		
		public MyDummyException(String message){
			super(message);
		}
	}
	
	@Remote
	@LocalExceptions({@LocalException(code=-5,exception=MyDummyException.class)})
	interface My11RemoteInterface{
							
		public Void op();// A object -> Remote operation op -> B object		
	}
	
	@Test
	public void testRemoteExceptionMapedLocalException() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-5,\"message\":\"Domain error\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My11RemoteInterface.class).op();
			assertTrue(false);
		}catch(MyDummyException ex){
						
			assertEquals("Domain error",ex.getMessage());
		}
	}
	
	@Remote
	@LocalExceptions({@LocalException(code=-5,exception=MyDummyException.class)})
	interface My12RemoteInterface{
							
		public Void op();// A object -> Remote operation op -> B object		
	}
	
	@Test
	public void testRemoteExceptionMapedLocalExceptionFailure() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-6,\"message\":\"Domain error\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		RpcProxy proxy = new RpcProxy(requester);						
		try{
			proxy.create(My12RemoteInterface.class).op();
			assertTrue(false);
		}catch(RemoteException ex){
						
			assertEquals(-6, ex.getCode());
			assertEquals("Domain error",ex.getMessage());
			
		}
	}
	

}
