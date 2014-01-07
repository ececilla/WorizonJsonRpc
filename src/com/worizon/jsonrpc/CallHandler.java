package com.worizon.jsonrpc;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.worizon.net.HttpRequest;

public class CallHandler{
	
	private Thread t;
	private WeakReference<HttpRequest> requestRef;
	private long beginCallTimeStamp = System.currentTimeMillis();
	private long endCalltimeStamp;
	private HttpRequest.State state = HttpRequest.State.RUNNING;
	
	CallHandler(){
		
		this.t = Thread.currentThread();		
	}
	
	CallHandler( Thread t ){
		
		this.t = t;		
	}
	
	CallHandler( HttpRequest request ){
		
		this();
		requestRef = new WeakReference<HttpRequest>(request); 
	}		
	
	public synchronized long getElapsedCallTime(){
		
		if( state == HttpRequest.State.RUNNING)
			return System.currentTimeMillis() - beginCallTimeStamp;
		else
			return endCalltimeStamp - beginCallTimeStamp;
	}		
	
	public void stop(){
				
		if( requestRef != null && requestRef.get() != null )
			requestRef.get().stop();
		
		t.interrupt();
	}
	
	<T> JsonRpcResponse<T> perform( JsonRpcRequest request, Class<T> clazz ) throws IOException, InterruptedException{
				
		String response = requestRef.get().perform( request.toString() );
		JsonRpcResponse<T> res =  new JsonRpcResponse<T>( response, clazz );
		return res;
		
	}
	
	synchronized void  setState( HttpRequest.State state ){
		
		this.state = state;
		if( state == HttpRequest.State.COMPLETE || state == HttpRequest.State.FAILED )
			endCalltimeStamp = System.currentTimeMillis();
	}
	
	public synchronized HttpRequest.State getState(){
		
		return state;
	}
		
}
