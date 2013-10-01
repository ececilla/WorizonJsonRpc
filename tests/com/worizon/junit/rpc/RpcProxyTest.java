package com.worizon.junit.rpc;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
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
	interface RemoteInterface{
		
		@RemoteParams(names={"x","y"})		
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
				
		@RemoteParams(names={"a"})		
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
