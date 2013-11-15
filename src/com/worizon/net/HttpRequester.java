package com.worizon.net;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.worizon.jsonrpc.TransformerException;

/**
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
public class HttpRequester {
			
	private static final int DEFAULT_READ_TIMEOUT = 15000;
	private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
	private static final int DEFAULT_CONNECT_RETRIES = 2;
	
	private String endpoint;
	private int nretries = DEFAULT_CONNECT_RETRIES;	
	private int readTimeout = DEFAULT_READ_TIMEOUT;	
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	private HttpURLConnection conn = null;
	private TransformerContext ctx = this.new TransformerContext();
	private List<ITransformer> transformers = new LinkedList<ITransformer>();
	
	public HttpRequester(){
				
	}
	
	public HttpRequester( String endpoint ){
		
		this.endpoint = endpoint;
		
	}
	
	public void setRequestRetries( int nretries ){
		
		this.nretries = nretries;
	}
	
	public int getRequestRetries(){
		
		return this.nretries;
	}
	
	public void setReadTimeout( int readTimeout ){
		
		this.readTimeout = readTimeout;
	}
	
	public int getReadTimeout(){
		
		return this.readTimeout;
	}
	
	public void setConnectTimeout( int connectTimeout ){
		
		this.connectTimeout = connectTimeout;
	}
	
	public int getConnectTimeout(){
		
		return connectTimeout;
	}
	
	public String getEndpoint(){
		
		return endpoint;
	}
	
	public void setEndpoint( String endpoint ){
		
		this.endpoint = endpoint;
	}
	
	void addTransformers( List<ITransformer> transformers ){
		
		this.transformers.addAll(transformers);
	}
			
	public void disconnect(){
		
		conn.disconnect();
		conn = null;
	}
	
	public String request() throws Exception{
		
		return request(null);
	}
				
	public String request( String body  ) throws InterruptedException, IOException{
							    
	    try{
	    	return readResponse( connectAndWriteRequest(body) );
	    }catch(IOException ex){
	    	
	    	disconnect();
	    	if( !Thread.interrupted() ){
		    	if(nretries-- > 0)
		    		return request(body);
		    	else
		    		throw ex;
	    	}else
	    		throw new InterruptedException();
	    }
	}
		
	
	private InputStream connectAndWriteRequest( String body ) throws MalformedURLException, IOException {
		
		//Transform payload and headers by delegating on transformers
		ctx.setBody(body);					
		for( ITransformer transformer:transformers ){
					
			if(ctx.skipNext())
				continue;
			try{
				transformer.transform( ctx );
			}catch(Throwable t){
				
				throw new TransformerException(t);
			}
			
			if( !ctx.shouldContinue() )
				break;
		}
		
		//Check body not null
		if(ctx.getBody() == null )
			throw new IllegalStateException("body not set");
		
		//Prepare connection
		URL url = new URL( endpoint );
		conn = (HttpURLConnection)url.openConnection();				
		conn.setDoOutput( true );		
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setDefaultUseCaches(false);
		conn.setUseCaches(false);
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		conn.setRequestMethod("POST");		
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Connection", "close");
				
		//Set HTTP headers
		for( Entry<String,String> entry: ctx.headers.entrySet() ){
			
			conn.setRequestProperty(entry.getKey(), entry.getValue());
		}
		conn.connect();		
		
		OutputStreamWriter writer = new OutputStreamWriter( conn.getOutputStream() );		
		writer.write( ctx.getBody() );		    													    
	    writer.flush();
	    try{
	    	
	    	return conn.getInputStream();
	    
	    }catch(FileNotFoundException fex){
	    	
	    	return conn.getErrorStream();
	    }
	}
					
	private String readResponse( InputStream is ) throws IOException {
		
		BufferedReader in = new BufferedReader( new InputStreamReader( is ) );		 		
		StringBuffer buffer = new StringBuffer();
								
		int cdata;
		while( (cdata = in.read()) != -1 ){
			
			buffer.append((char) cdata );
		}
		//is.close();//should close inputstream to close connection.
		return buffer.toString();
		
	}
	
	/**
	 * 
	 */
	public class TransformerContext{
		
		private String body;
		private Map<String,String> headers = new HashMap<String,String>();
		private boolean shouldContinue = true;
		private boolean skipNext = false;
		
		public void putHeader( String headerKey, String headerValue ){
			
			headers.put(headerKey, headerValue);
		}
		
		public String getHeader( String headerKey ){
			
			return headers.get(headerKey);
		}
		
		public String getBody(){
			
			return body;
		}
		
		public void setBody( String body ){
													
			this.body = body;
			
		}
		
		public boolean skipNext(){
			
			return skipNext;
		}
		
		public void skipNext( boolean skipNext ){
			
			this.skipNext = skipNext;
		}
		
		public boolean shouldContinue(){
			
			return shouldContinue;
		}
		
		public void shouldContinue( boolean shouldContinue ){
			
			this.shouldContinue = shouldContinue;
		}
		
	}
	
	/**
	 * Transformer interface
	 */
	public interface ITransformer{
		
		public void transform( TransformerContext ctx ) throws Throwable;
		
	}
		
}
