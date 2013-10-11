package com.worizon.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 */
public class HttpRequester {
			
	private String endpoint;
	private int nretries = 2;
	private final int DEFAULT_READ_TIMEOUT = 15000;
	private final int DEFAULT_CONNECT_TIMEOUT = 10000;	
	private HttpURLConnection conn = null;
	private TransformerContext ctx  = this.new TransformerContext();
	private List<ITransformer> transformers = new LinkedList<ITransformer>();
	
	public HttpRequester(){
				
	}
	
	public HttpRequester( String endpoint ){
		
		this.endpoint = endpoint;
		
	}
	
	void addTransformers( List<ITransformer> transformers ){
		
		this.transformers.addAll(transformers);
	}
			
	public void disconnect(){
		
		conn.disconnect();
		conn = null;
	}	
				
	public String request( String body, int readTimeout ) throws MalformedURLException, IOException, InterruptedException{
							    
	    try{
	    	return readResponse( connectAndWriteRequest(body, readTimeout) );
	    }catch(IOException ex){
	    	
	    	disconnect();
	    	if( !Thread.interrupted() ){
		    	if(nretries-- > 0)
		    		return request(body, readTimeout);
		    	else
		    		throw ex;
	    	}else
	    		throw new InterruptedException();
	    }
	}
	
	public String request( String body ) throws MalformedURLException, IOException, InterruptedException{
				
		return request(body, DEFAULT_READ_TIMEOUT);
	}
	
	private InputStream connectAndWriteRequest( String payload, int readTimeout) throws MalformedURLException, IOException {
		
		//Transform payload and headers by delegating on transformers
		ctx.setPayload(payload);
		for( ITransformer transformer:transformers ){
			
			if(ctx.skipNext())
				continue;
			
			transformer.transform( ctx );
			
			if( !ctx.shouldContinue() )
				break;
		}
		
		//Prepare connection
		URL url = new URL( endpoint );
		conn = (HttpURLConnection)url.openConnection();				
		conn.setDoOutput( true );
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setDefaultUseCaches(false);
		conn.setUseCaches(false);
		conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
		conn.setReadTimeout(readTimeout);
		conn.setRequestMethod("POST");		
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
				
		//Set HTTP headers
		for( Entry<String,String> entry: ctx.headers.entrySet() ){
			
			conn.setRequestProperty(entry.getKey(), entry.getValue());
		}
		conn.connect();		
		
		OutputStreamWriter writer = new OutputStreamWriter( conn.getOutputStream() );		
		writer.write( ctx.getPayload() );		    													    
	    writer.flush();
	    try{
	    	
	    	return conn.getInputStream();
	    
	    }catch(FileNotFoundException fex){
	    	
	    	return conn.getErrorStream();
	    }
	}
					
	private String readResponse( InputStream is ) throws IOException{
		
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
		
		private String payload;
		private Map<String,String> headers = new HashMap<String,String>();
		private boolean shouldContinue = true;
		private boolean skipNext = false;
		
		public void putHeader( String headerKey, String headerValue ){
			
			headers.put(headerKey, headerValue);
		}
		
		public String getHeader( String headerKey ){
			
			return headers.get(headerKey);
		}
		
		public String getPayload(){
			
			return payload;
		}
		
		public void setPayload( String payload ){
			
			this.payload = payload;
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
	 * 
	 */
	public interface ITransformer{
		
		public void transform( TransformerContext ctx );
		
	}
		
}
