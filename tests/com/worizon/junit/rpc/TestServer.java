package com.worizon.junit.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.worizon.net.HttpRequester;



/**
 *  Servidor single-thread TCP para hacer dump de lo que mandan los clientes.
 */
public class TestServer extends BaseServer {
		
	private Map<String,String> headers = new LinkedHashMap<String,String>();
	private IRequester adapter;
	private String body;
	private String command;
	
	public TestServer( int port ) throws IOException {
				
		super( port );
		start();
	}
	
	/**
	 * Get the request's body payload sent.
	 */
	public String getBody(){
		
		return body;
	}
	
	/**
	 * Get the request's command sent.
	 */
	public String getCommand(){
		
		return command;
	}
	
	/**
	 * Print request's headers sent.
	 */
	public void dumpHeaders(){
		
		for( Entry<String,String> entry: getHeaders().entrySet()){
			
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
	
	/**
	 * Get request's headers sent.
	 */
	public Map<String,String> getHeaders(){
		
		return headers;
	}
				
	/**
	 * Build the wrapped version of the adapter to make the request.
	 */
	public IRequester createTestRequester(String endpoint){
		
		return this.new NonResponseReadRequester(endpoint);
	}
	
	/**
	 * Build the wrapped version of the adapter to make the request.
	 */
	public IRequester createTestRequester(){
		
		return this.new NonResponseReadRequester();
	}
	
	/**
	 * Set the requester adapter for this server. 
	 */	
	public void setAdapter( IRequester adapter ){
		
		this.adapter = adapter;
	}	
		
	
	@Override
	protected  void readStream( InputStream is ) throws IOException {
		
		int numLines = 0;
		String inputLine;
		BufferedReader in  =  new BufferedReader(new InputStreamReader( is ));							
		while ((inputLine = in.readLine()) != null) {   									
			
			if(++numLines == 1){
				command = inputLine;
			}else{
				if( inputLine.isEmpty() ){					    	
			    	
					body = in.readLine();				
					in.close();
					finish();
					break;
			    }else{			    	
			    	
			    	int colorSeparatorIndex = inputLine.indexOf(':');
					String key = inputLine.substring(0, colorSeparatorIndex).trim();
					String value = inputLine.substring(colorSeparatorIndex+1).trim();
					headers.put(key, value);					
			    }
			}
		}					
	}
	
	public interface IRequester{
		
		public void setEndpoint(String endpoint);
		public void request(String body) throws Exception;
	}
	
	/**
	 * The adapter object is wrapped with an object of this class. This class is responsible 
	 * for making the request through the adapter and muting the response read stream exceptions because 
	 * of a premature connection close at server.
	 * 
	 */
	private class NonResponseReadRequester implements IRequester{
		
		public NonResponseReadRequester(){}
		
		public NonResponseReadRequester(String endpoint){
			
			if(adapter == null)
				throw new IllegalStateException("Request adapter not set");
			
			adapter.setEndpoint(endpoint);
		}	
				
		public void setEndpoint( String endpoint){
			
			adapter.setEndpoint(endpoint);
		}
		
		public void request( String body ){
			
			try{
				if( !body.endsWith("\n") )
					body += "\n";					
				adapter.request(body); 
			}catch(Exception ex){}
			finally{
				
				try{TestServer.this.join();}catch(InterruptedException ie){}				
			}						
		}						
				
	}
	
}
	
