package com.worizon.net;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import com.worizon.net.HttpRequest.ITransformer;
import com.worizon.net.HttpRequest.TransformerContext;
import static com.worizon.jsonrpc.Const.Http.DEFAULT_READ_TIMEOUT;
import static com.worizon.jsonrpc.Const.Http.DEFAULT_CONNECT_TIMEOUT;
import static com.worizon.jsonrpc.Const.Http.DEFAULT_CONNECT_RETRIES;

/**
 * Builder pattern to construct HttpRequester objects.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
final public class HttpRequestBuilder {
	
	/**
	 * Delegate object to make http requests.
	 */
	private  HttpRequest request;
	
	/**
	 * URL endpoint.
	 */
	private String endpoint;
	
	/**
	 * Transformers that will transform the request somehow.
	 */
	private  List<ITransformer> transformers = new LinkedList<ITransformer>();
	
	/**
	 * Connection timeout setting.
	 */
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	
	/**
	 * Read timeout setting.
	 */
	private int readTimeout = DEFAULT_READ_TIMEOUT;	
	
	/**
	 * Connection retries. 
	 */
	private int nRetries = DEFAULT_CONNECT_RETRIES;
	
	public HttpRequestBuilder( HttpRequest request ){
		
		this.request = request;		
	}
	
	public HttpRequestBuilder(){}
	
	/**
	 * Sets the endpoint for the HttpRequester object.
	 * @param endpoint The endpoint which the request will be targeted at.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder endpoint( String endpoint ){
		
		this.endpoint = endpoint;
		return this;
	}
		
	
	/**
	 * Sets the connect timeout for the HttpRequester object.
	 * @param connectTimeout The connection timeout in ms.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder connectTimeout( int connectTimeout ){
		
		this.connectTimeout = connectTimeout;
		return this;
	}
	
	/**
	 * Sets the read timeout for the HttpRequester object.
	 * @param connectTimeout The read timeout in ms.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder readTimeout( int readTimeout ){
		
		this.readTimeout = readTimeout;
		return this;
	}
	
	/**
	 * Sets the connection retries.
	 * @param nRetries Number of retries.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder requestRetries( int nRetries ){
		
		this.nRetries = nRetries;
		return this;
	}
	
	/**
	 * Adds a transformer to the transformers chain to encode the body content as URL.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder bodyURLEncode(){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform( TransformerContext ctx ){
				
				String body = ctx.getBody();							
				try{
					ctx.setBody( URLEncoder.encode(body, "UTF-8") );
				}catch(UnsupportedEncodingException uee){
					
					throw new RuntimeException(uee);
				}
			}
		});
		
	}
	
	/**
	 * Adds a transformer to the transformers chain to trim the body content.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder bodyTrim(){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform(TransformerContext ctx){
				
				ctx.setBody( ctx.getBody().trim() );
			}
		});
	}
	
	/**
	 * Adds a transformer to the transformers chain to concat the body content with
	 * another String.
	 * @param trailingString the String that will be appended to the end of the body.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder bodyConcat( final String trailingString ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform( TransformerContext ctx ) {
				
				String body = ctx.getBody();
				ctx.setBody( body.concat( trailingString ) );
				
			}
		});		
	}
	
	/**
	 * Adds a transformer to the transformers chain to prepend the body concat with a string.
	 * @param leadingString The string that will be prepended in front of the body content.
	 * @return Builder object to keep building.
	 */
	public HttpRequestBuilder bodyPrepend( final String leadingString ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform( TransformerContext ctx ) {
				
				String body = ctx.getBody();
				ctx.setBody( leadingString.concat(body) );
				
			}
		});		
	}
	
	/**
	 * 
	 */
	public HttpRequestBuilder continueIfTrue( final boolean condition ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform(TransformerContext ctx) {
				
				ctx.shouldContinue(condition);
			}
		});		
	}
	
	public HttpRequestBuilder skipNextIfTrue( final boolean condition ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform(TransformerContext ctx) {
				
				ctx.skipNext(condition);
			}
		});
	}
	
	public HttpRequestBuilder addTransformer( ITransformer transformer ){
		
		transformers.add(transformer);
		return this;
	}	
	
	public HttpRequest build() throws MalformedURLException{
		
		HttpRequest newRequest = request;
		if( newRequest == null ){
			if( endpoint == null )
				throw new IllegalStateException("endpoint not set");			
			newRequest = new HttpRequest(endpoint);
		}else if( endpoint != null )
			newRequest.setEndpoint(endpoint);
		else if( newRequest.getEndpoint() == null )
			throw new IllegalStateException("endpoint not set");												
		
		newRequest.addTransformers(transformers);
		newRequest.setConnectTimeout(connectTimeout);
		newRequest.setReadTimeout(readTimeout);
		newRequest.setRequestRetries(nRetries);
		return newRequest;
	}

}
