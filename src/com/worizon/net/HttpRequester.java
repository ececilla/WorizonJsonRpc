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

import org.apache.commons.validator.routines.UrlValidator;

import static com.worizon.jsonrpc.Const.Http.DEFAULT_CONNECT_RETRIES;
import static com.worizon.jsonrpc.Const.Http.DEFAULT_READ_TIMEOUT;
import static com.worizon.jsonrpc.Const.Http.DEFAULT_CONNECT_TIMEOUT;
import com.worizon.jsonrpc.TransformerException;

/**
 * Class to make HTTP POST requests.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
public class HttpRequester {
							
	private URL endpoint;
	private int nretries = DEFAULT_CONNECT_RETRIES;	
	private int readTimeout = DEFAULT_READ_TIMEOUT;	
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	private HttpURLConnection conn = null;
	private TransformerContext ctx = this.new TransformerContext();
	private List<ITransformer> transformers = new LinkedList<ITransformer>();
	
	public HttpRequester(){
				
	}
	
	/**
	 * Instantiates an HttpRequester with the specified endpoint
	 * @param endpoint The endpoint of the remote procedure.
	 */
	public HttpRequester( String endpoint ) throws MalformedURLException{
		
		setEndpoint(endpoint);		
	}
	
	/**
	 * Sets the number of failed requests before dropping the reconnection loop.
	 * @param nretries Number of retries.
	 */
	public void setRequestRetries( int nretries ){
		
		this.nretries = nretries;
	}
	
	/**
	 * Gets the number of retries.
	 * @return The number of retries.
	 */
	public int getRequestRetries(){
		
		return this.nretries;
	}
	
	/**
	 * Sets the number of milliseconds to wait for reading data before dropping the connection 
	 * and throw a readtimeout exception.
	 * @param readTimeout Number of millis for read timeout.
	 */
	public void setReadTimeout( int readTimeout ){
		
		this.readTimeout = readTimeout;
	}
	
	/**
	 * Gets the number of milliseconds to wait for data to be read.
	 * @return Read timeout milliseconds.
	 */
	public int getReadTimeout(){
		
		return this.readTimeout;
	}
	
	/**
	 * Sets the number of milliseconds the connection attempt will be hold.
	 * @param connectTimeout Number of 
	 */
	public void setConnectTimeout( int connectTimeout ){
		
		this.connectTimeout = connectTimeout;
	}
	
	/**
	 * Gets the number of milliseconds the connection attempt will be hold.
	 * @return The number of milliseconds.
	 */
	public int getConnectTimeout(){
		
		return connectTimeout;
	}
	
	/**
	 * Gets the rpc endpoint this object will make requests.
	 * @return rpc endpoint
	 */
	public URL getEndpoint(){
		
		return endpoint;
	}
	
	/**
	 * Sets the rpc endpoint to which this object will try to make requests.
	 * @param endpoint The rpc endpoint.
	 * @throws MalformedURLException When the URL string is not valid.
	 */
	public void setEndpoint( String endpoint ) throws MalformedURLException{
				
		UrlValidator validator = new UrlValidator(new String[]{"http"},UrlValidator.ALLOW_LOCAL_URLS);
		if( validator.isValid(endpoint ))
			this.endpoint = new URL(endpoint);
		else
			throw new MalformedURLException();
		
	}
	
	/**
	 * Adds a transformer list to the chain of transformers.
	 * @param transformers
	 */
	public void addTransformers( List<ITransformer> transformers ){
		
		this.transformers.addAll(transformers);
	}
	
	/**
	 * Gets transformers for this requester object.
	 * @return transformers list.
	 */
	public List<ITransformer> getTransformers(){
		
		return this.transformers;
	}
	
	/**
	 * Diconnects from endpoint.
	 */
	public void disconnect(){
				
		conn.disconnect();
		conn = null;
	}
	
	/**
	 * Makes a request with a null body.
	 * @return response body.
	 */
	public String request() throws Exception{
		
		return request(null);
	}
	
	/**
	 * Makes a request with the specified body.
	 * @param body The body that will be sent as POST payload.
	 * @return The body response from the server.
	 */
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
		
	/**
	 * Connects and writes the request to the server.
	 * @param body The body to be sent to the server. 
	 * @return The read stream that resulted from connecting to the server.  
	 */
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
		conn = (HttpURLConnection)endpoint.openConnection();				
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
	
	/**
	 * Reads the response from the input stream.
	 * @param is server's input stream.
	 * @return The body content of the response 
	 */
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
	 * This class represents the context of a request body transformation. All transformers receive
	 * a transformer object as parameter, through this context the transformer has access to the
	 * request content and request headers in order to read or manipulate somehow.
	 */
	public class TransformerContext{
		
		/**
		 * Body contents of the request. 
		 */
		private String body;
		
		/**
		 * Http headers.
		 */
		private Map<String,String> headers = new HashMap<String,String>();
		
		/**
		 * When a TransformatoinContext object has this flag set to false the transformation loop stops at this transformer.
		 */
		private boolean shouldContinue = true;
		
		/**
		 * When a TransformationContext object has this flag set to true the transformation loop skips the next transformation
		 * step.
		 */
		private boolean skipNext = false;
		
		/**
		 * Adds an HTTP header to this context object.
		 * @param headerKey header key.
		 * @param headerValue header value.
		 */
		public void putHeader( String headerKey, String headerValue ){
			
			headers.put(headerKey, headerValue);
		}
		
		/**
		 * Gets the HTTP header by key.
		 * @param headerKey header key.
		 * @return The value of this header.
		 */
		public String getHeader( String headerKey ){
			
			return headers.get(headerKey);
		}
		
		/**
		 * Gets the current value of request body.
		 */
		public String getBody(){
			
			return body;
		}
		
		/**
		 * Sets the value of the request body.
		 */
		public void setBody( String body ){
													
			this.body = body;
			
		}
		
		/**
		 * Checks if the transformation loop should skip next transformer.
		 */
		public boolean skipNext(){
			
			return skipNext;
		}
		
		/**
		 * Sets the next transformer in the transformation chain to be skipped.
		 */
		public void skipNext( boolean skipNext ){
			
			this.skipNext = skipNext;
		}
		
		/**
		 * Checks if the transformation loop should continue with the whole transformation.
		 */
		public boolean shouldContinue(){
			
			return shouldContinue;
		}
		
		/**
		 * Sets the transformation loop to continue transforming or not.
		 */
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
