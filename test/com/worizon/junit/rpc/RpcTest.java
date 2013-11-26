package com.worizon.junit.rpc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcException;
import com.worizon.jsonrpc.JsonRpcRequest;
import com.worizon.jsonrpc.RemoteException;
import com.worizon.jsonrpc.Rpc;
import com.worizon.jsonrpc.annotations.LocalException;
import com.worizon.jsonrpc.annotations.LocalExceptions;
import com.worizon.jsonrpc.annotations.Remote;
import com.worizon.jsonrpc.annotations.RemoteParams;
import com.worizon.jsonrpc.annotations.RemoteProcName;
import com.worizon.net.HttpRequester;


public class RpcTest {
	
	@Test
	public void testRemoteParam(){
			
		Map.Entry<String, Object> pair = Rpc.RemoteParam("paramName", "paramValue");
		assertEquals( "paramName", pair.getKey() );
		assertEquals( "paramValue", pair.getValue() );
		
	}
	
	interface NonRemoteInterface{};
	
	@Test(expected = IllegalArgumentException.class)	
	public void testNonRemoteInterface(){
		
		HttpRequester http = new HttpRequester("http://localhost:8080/rpc");
		Rpc proxy = new Rpc(http);
		NonRemoteInterface remote = proxy.createProxy(NonRemoteInterface.class);
		
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
				
		Rpc proxy = new Rpc(requester);
		My1RemoteInterface remote = proxy.createProxy(My1RemoteInterface.class);
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
		Rpc proxy = new Rpc(requester);
		My2RemoteInterface remote = proxy.createProxy(My2RemoteInterface.class);
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
		Rpc proxy = new Rpc(requester);
		My3RemoteInterface remote = proxy.createProxy( My3RemoteInterface.class);
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
		Rpc proxy = new Rpc(requester);
		My4RemoteInterface remote = proxy.createProxy( My4RemoteInterface.class);
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
				
		Rpc proxy = new Rpc(requester);
		My5RemoteInterface remote = proxy.createProxy(My5RemoteInterface.class);
				
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
				
		Rpc proxy = new Rpc(requester);
		My6RemoteInterface remote = proxy.createProxy(My6RemoteInterface.class);
				
		assertEquals(9, remote.sum(5, 4));
	}	
	
	
	
	class A{
		int x;
		int y;
		B b = null;
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
				
		Rpc proxy = new Rpc(requester);
		My7RemoteInterface remote = proxy.createProxy(My7RemoteInterface.class);
		
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
				
		Rpc proxy = new Rpc(requester);
		My8RemoteInterface remote = proxy.createProxy(My8RemoteInterface.class);
		
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
				
		Rpc proxy = new Rpc(requester);
		My9RemoteInterface remote = proxy.createProxy(My9RemoteInterface.class);
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My10RemoteInterface.class).op();
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My10RemoteInterface.class).op();
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My10RemoteInterface.class).op();
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My10RemoteInterface.class).op();
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My10RemoteInterface.class).op();
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My10RemoteInterface.class).op();
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
							
		public Void op();		
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My11RemoteInterface.class).op();
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
		Rpc proxy = new Rpc(requester);						
		try{
			proxy.createProxy(My12RemoteInterface.class).op();
			assertTrue(false);
		}catch(RemoteException ex){
						
			assertEquals(-6, ex.getCode());
			assertEquals("Domain error",ex.getMessage());
			
		}
	}
	
	@Remote	
	interface My13RemoteInterface{
							
		public Void op();		
	}
	
	@Test
	public void testCall1() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": {}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		rpc.callVoid("op");		
	}
	
	@Test
	public void testCall2() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-6,\"message\":\"Domain error\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);						
		try{
			
			rpc.call("op" ,Void.class);
			assertTrue(false);
		}catch(RemoteException ex){
						
			assertEquals(-6, ex.getCode());
			assertEquals("Domain error",ex.getMessage());
			
		}
	}
	
	@Test
	public void testCall3() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-6,\"message\":\"Domain exception\"}, \"id\": 2}";
				
			}
		});
				
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);						
		try{
			rpc.addRuntimeExceptionMapping(-6, ArithmeticException.class);
			rpc.call("op" ,Void.class);
			assertTrue(false);
		}catch(ArithmeticException ex){
									
			assertEquals("Domain exception",ex.getMessage());
			
		}
	}
	
	@Test
	public void testCallVoid() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": {}, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		rpc.callVoid("op");		
		
	}
		
	
	@Test
	public void testCallVoidOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[1.5,\"test\"],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": {}, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		rpc.callVoid("op",1.5,"test");		
		
	}
	
	@Test
	public void testCallVoidNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":1.5,\"p2\":\"test\"},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": {}, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		rpc.callVoid("op",Rpc.RemoteParam("p1", 1.5),Rpc.RemoteParam("p2", "test"));		
		
	}
	
	@Test
	public void testCallVoidNamedParamsWithException() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"v1\":1.5,\"v2\":\"test\"},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": {}, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);	
		try{
			rpc.callVoid("op",Rpc.RemoteParam("v1", 1.5),"test");		
			fail();
		}catch(IllegalArgumentException iae){
			
			assertEquals("Must pass ALL parameters as named parameters or NONE.",iae.getMessage());
		}
	}
	
	@Test
	public void testCallInteger() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		int result = rpc.callInteger("op");		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallIntegerOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[false,\"test\"],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		int result = rpc.callInteger("op",false,"test");		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallIntegerNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":false,\"p2\":\"test\"},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		int result = rpc.callInteger("op",Rpc.RemoteParam("p1", false),Rpc.RemoteParam("p2", "test"));		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallIntegerArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [10,34,23], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		int[] result = rpc.callIntegerArray("op");		
		assertArrayEquals(new int[]{10,34,23}, result);
	}
	
	@Test
	public void testCallIntegerArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"mult10\",\"params\":[true,[1,2,3]],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [10,20,30], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		int[] result = rpc.callIntegerArray("mult10",true,new int[]{1,2,3});		
		assertArrayEquals(new int[]{10,20,30}, result);
	}
	
	@Test
	public void testCallDouble() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": 1000000, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		double result = rpc.callDouble("op");		
		assertTrue( result == 1000000 );
	}
	
	@Test
	public void testCallDoubleOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[true],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 1000000, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		double result = rpc.callDouble("op",true);		
		assertTrue( result == 1000000 );
	}
	
	@Test
	public void testCallDoubleNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":true},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 1000000, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		double result = rpc.callDouble("op",Rpc.RemoteParam("p1", true));		
		assertTrue( result == 1000000 );
	}
	
	@Test
	public void testCallDoubleArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [10.1,34.4,23.5], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		double[] result = rpc.callDoubleArray("op");		
		assertArrayEquals(new double[]{10.1,34.4,23.5}, result, 0.000001);
		
	}
	
	@Test
	public void testCallDoubleArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[null],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [10.1,34.4,23.5], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		double[] result = rpc.callDoubleArray("op", new Object[]{null});		
		assertArrayEquals(new double[]{10.1,34.4,23.5}, result, 0.000001);
		
	}
	
	@Test
	public void testCallFloat() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": 34.5677, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		float result = rpc.callFloat("op");		
		assertTrue( result == 34.5677f );
	}
	
	@Test
	public void testCallFloatOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[true],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 65.3482374, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		float result = rpc.callFloat("op",true);		
		assertTrue( result == 65.3482374f );
	}
	
	@Test
	public void testCallFloatNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":true},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 65.3482374, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		float result = rpc.callFloat("op",Rpc.RemoteParam("p1", true));		
		assertTrue( result == 65.3482374f );
	}
	
	@Test
	public void testCallFloatArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [10.1,34.4,23.5], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		float[] result = rpc.callFloatArray("op");		
		assertArrayEquals(new float[]{10.1f,34.4f,23.5f}, result, 0.0000001f);		
		
	}
	
	@Test
	public void testCallFloatArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[null],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [10.1,34.4,23.5], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		float[] result = rpc.callFloatArray("op", new Object[]{null});		
		assertArrayEquals(new float[]{10.1f,34.4f,23.5f}, result, 0.000001f);
		
	}
	
	@Test
	public void testCallString() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": \"foobar\", \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		String result = rpc.callString("op");		
		assertEquals("foobar", result);
	}
	
	@Test
	public void testCallStringOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[true],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": \"foobar\", \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		String result = rpc.callString("op",true);		
		assertEquals("foobar",result);
	}
	
	@Test
	public void testCallStringNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":true},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": \"foobar\", \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		String result = rpc.callString("op",Rpc.RemoteParam("p1", true));		
		assertEquals("foobar",result);
	}
	
	@Test
	public void testCallStringArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [\"foo\",\"bar\",\"dummy\"], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		String[] result = rpc.callStringArray("op");		
		assertArrayEquals(new String[]{"foo","bar","dummy"}, result);		
		
	}
	
	@Test
	public void testCallStringArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();						
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[{\"a\":1,\"b\":\"baz\"}],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [\"foo\",\"bar\",\"dummy\"], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);
		Map<String,Object> params = new LinkedHashMap<String, Object>();
		params.put("a",1);
		params.put("b","baz");
		String[] result = rpc.callStringArray("op", params);		
		assertArrayEquals(new String[]{"foo","bar","dummy"}, result);
		
	}
	
	@Test
	public void testCallBoolean() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": true, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		boolean result = rpc.callBoolean("op");		
		assertTrue( result == true );
	}
	
	@Test
	public void testCallBooleanOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[1,2,3,\"foo\"],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": false, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		boolean result = rpc.callBoolean("op",1,2,3, "foo" );		
		assertTrue( result == false );
	}
	
	@Test
	public void testCallBooleanNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":1,\"p2\":2,\"p3\":3,\"p4\":\"foo\"},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": false, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		boolean result = rpc.callBoolean("op",Rpc.RemoteParam("p1", 1),Rpc.RemoteParam("p2", 2),Rpc.RemoteParam("p3", 3),Rpc.RemoteParam("p4", "foo"));		
		assertTrue( result == false );
	}
	
	@Test
	public void testCallBooleanArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [true,true,false], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		boolean[] result = rpc.callBooleanArray("op");		
		assertTrue( Arrays.equals(new boolean[]{true,true,false}, result ) );
					
		
	}
	
	@Test
	public void testCallBooleanArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[5],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [true,true,false], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		boolean[] result = rpc.callBooleanArray("op", 5);		
		assertTrue( Arrays.equals(new boolean[]{true,true,false}, result ) );
		
	}
	
	@Test
	public void testCallShort() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		short result = rpc.callShort("op");		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallShortOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[false,\"test\"],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		short result = rpc.callShort("op",false,"test");		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallShortNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":false,\"p2\":\"test\"},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		short result = rpc.callShort("op",Rpc.RemoteParam("p1", false),Rpc.RemoteParam("p2", "test"));		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallShortArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [10,34,23], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		short[] result = rpc.callShortArray("op");		
		assertArrayEquals(new short[]{10,34,23}, result);
	}
	
	@Test
	public void testCallShortArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"mult10\",\"params\":[true,false],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [10,20,30], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		short[] result = rpc.callShortArray("mult10",true,false);		
		assertArrayEquals(new short[]{10,20,30}, result);
	}
	
	@Test
	public void testCallLong() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		long result = rpc.callLong("op");		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallLongOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[false,\"test\"],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		long result = rpc.callLong("op",false,"test");		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallLongNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertFalse(request.startsWith("{\"method\":\"op\",\"params\":[false,\"test\"],\"jsonrpc\":\"2.0\","));
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":false,\"p2\":\"test\"},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": 10, \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		long result = rpc.callLong("op",Rpc.RemoteParam("p1", false),Rpc.RemoteParam("p2", "test"));		
		assertTrue( result == 10 );
	}
	
	@Test
	public void testCallLongArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [10,34,23], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		long[] result = rpc.callLongArray("op");		
		assertArrayEquals(new long[]{10,34,23}, result);
	}
	
	@Test
	public void testCallLongArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"mult10\",\"params\":[true,false],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [10,20,30], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		long[] result = rpc.callLongArray("mult10",true,false);		
		assertArrayEquals(new long[]{10,20,30}, result);
	}
	
	@Test
	public void testCallChar() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": \"a\", \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		char result = rpc.callChar("op");		
		//System.out.println(String.format("%04x", (int) result));
		assertTrue( result == 0x61 );
		assertTrue( result == 'a' );
	}
	
	@Test
	public void testCallCharOrderedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[false,\"test\"],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": \"a\", \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		char result = rpc.callChar("op",false,"test");		
		assertTrue( result == 'a' );
	}
	
	@Test
	public void testCallCharNamedParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();					
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":{\"p1\":false,\"p2\":\"test\"},\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": \"a\", \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		char result = rpc.callChar("op",Rpc.RemoteParam("p1", false),Rpc.RemoteParam("p2", "test"));		
		assertTrue( result == 'a' );
	}
	
	@Test
	public void testCallCharArray() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);		
		EasyMock.expect(requester.request( EasyMock.anyString() ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				return "{\"jsonrpc\": \"2.0\", \"result\": [\"a\",\"b\",\"c\"], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);										
		char[] result = rpc.callCharArray("op");		
		assertArrayEquals(new char[]{'a','b','c'}, result);
	}
	
	@Test
	public void testCallCharArrayParams() throws Exception{
		
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);	
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.request( EasyMock.capture(requestCapture) ))		
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{												
				
				String request = requestCapture.getValue();												
				assertTrue(request.startsWith("{\"method\":\"op\",\"params\":[1,{\"x\":2,\"y\":3,\"b\":{\"z\":\"bar\",\"f\":5.6}}],\"jsonrpc\":\"2.0\","));
				return "{\"jsonrpc\": \"2.0\", \"result\": [\"a\",\"b\",\"c\"], \"id\": 2}";
				
			}
		});						
		EasyMock.replay(requester);				
		Rpc rpc = new Rpc(requester);	
		A a = new A(2,3);
		a.b = new B("bar",5.6f);
		char[] result = rpc.callCharArray("op",1,a);		
		assertArrayEquals(new char[]{'a','b','c'}, result);
	}
	

}
