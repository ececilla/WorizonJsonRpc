package com.worizon.junit.rpc;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

import com.worizon.jsonrpc.Remote;
import com.worizon.jsonrpc.RemoteParams;
import com.worizon.jsonrpc.RemoteProcName;
import com.worizon.net.HttpRequester;
import com.worizon.net.RpcProxy;

public class RpcProxyTest {
	
	interface NonRemoteInterface{};
	
	@Test(expected = IllegalArgumentException.class)	
	public void testNonRemoteInterface(){
		
		HttpRequester http = new HttpRequester("http://localhost:8080/rpc");
		RpcProxy proxy = new RpcProxy(http);
		NonRemoteInterface remote = proxy.create(NonRemoteInterface.class);
		
	}
	
	@Remote
	interface MyRemoteInterface{
		public void test();
	};
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonAnotattedParams() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		EasyMock.expect(requester.sendSyncPostRequest( (String)EasyMock.anyObject() )).andReturn("{\"jsonrpc\": \"2.0\", \"result\": 9, \"id\": 2}");
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		MyRemoteInterface remote = proxy.create(MyRemoteInterface.class);
		remote.test();
	}
	
	@Remote
	interface MySecondRemoteInterface{
		
		@RemoteParams({"x","y","z"})
		public void test(int x, int y);
	};
	
	@Test(expected=IllegalArgumentException.class)
	public void testAnnottedParamsNumberMismath() throws Exception{
		
		HttpRequester requester = new HttpRequester();										
		RpcProxy proxy = new RpcProxy(requester);
		MySecondRemoteInterface remote = proxy.create(MySecondRemoteInterface.class);
		remote.test(1,2);
	}
	
	@Remote
	interface MyThirdRemoteInterface{
		
		@RemoteParams({"params"})
		public Void test( Map<String, Object> params );
	}
		
	@Test
	public void testRemoteInterfaceWithHashMapParam() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		final Capture<String> requestCapture = new Capture<String>();
		EasyMock.expect(requester.sendSyncPostRequest( EasyMock.capture(requestCapture) ))
		.andAnswer(new IAnswer<String>() {
			
			public String answer() throws Throwable{
				
				String request = requestCapture.getValue();					
				assertTrue( request.startsWith("{\"method\":\"test\",\"params\":{\"x\":1,\"y\":\"test string\"},\"jsonrpc\":\"2.0\"") );
				return "{\"jsonrpc\": \"2.0\", \"result\":{} , \"id\": 2}";
				
			}
		})
		.anyTimes();
		EasyMock.replay(requester);
		RpcProxy proxy = new RpcProxy(requester);
		MyThirdRemoteInterface remote = proxy.create( MyThirdRemoteInterface.class);
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("x", 1);
		params.put("y","test string");
		remote.test(params);
		
	}
	
	
	@Remote
	interface RemoteInterface{
		
		@RemoteParams({"x","y"})		
		public int sum( int x, int y);
	}
	
	@Test
	public void testRemoteInterface() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		EasyMock.expect(requester.sendSyncPostRequest( (String)EasyMock.anyObject() )).andReturn("{\"jsonrpc\": \"2.0\", \"result\": 9, \"id\": 2}");
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		RemoteInterface remote = proxy.create(RemoteInterface.class);
				
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
	interface RemoteInterfaceWithObject{
				
		@RemoteParams({"a"})		
		public B op( A a );// A object -> Remote operation op -> B object
		
	}
	
	@Test
	public void testRemoteInterfaceWithObjects() throws Exception{
		
		HttpRequester requester = EasyMock.createMock(HttpRequester.class);
		EasyMock.expect(requester.sendSyncPostRequest( (String)EasyMock.anyObject() )).andReturn("{\"jsonrpc\": \"2.0\", \"result\": {\"z\":\"test\",\"f\":23.45}, \"id\": 2}");
		EasyMock.replay(requester);
				
		RpcProxy proxy = new RpcProxy(requester);
		RemoteInterfaceWithObject remote = proxy.create(RemoteInterfaceWithObject.class);
		
		A a = new A(2,3);
		B expected = new B("test",23.45f);
		assertEquals(expected, remote.op(a));
	}

}
