package com.worizon.net;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import com.worizon.net.HttpRequester.ITransformer;
import com.worizon.net.HttpRequester.TransformerContext;

/**
 * Builder pattern to construct HttpRequester objects.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
final public class HttpRequesterBuilder {
	
	/**
	 * Delegate object to make http requests.
	 */
	private  HttpRequester requester;
	
	/**
	 * URL endpoint.
	 */
	private String endpoint;
	
	/**
	 * Transformers that will transform the request somehow.
	 */
	private  List<ITransformer> transformers = new LinkedList<ITransformer>();
		
	
	public HttpRequesterBuilder( HttpRequester requester ){
		
		this.requester = requester;		
	}
	
	public HttpRequesterBuilder(){}
	
	/**
	 * Sets the endpoint for the HttpRequester object.
	 * @param endpoint The endpoint which the request will be targeted at.
	 * @return Builder object to keep building.
	 */
	public HttpRequesterBuilder endpoint( String endpoint ){
		
		this.endpoint = endpoint;
		return this;
	}
	
	/**
	 * Adds a transformer to the transformers chain to encode the body content as URL.
	 * @return Builder object to keep building.
	 */
	public HttpRequesterBuilder bodyURLEncode(){
		
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
	public HttpRequesterBuilder bodyTrim(){
		
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
	public HttpRequesterBuilder bodyConcat( final String trailingString ){
		
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
	public HttpRequesterBuilder bodyPrepend( final String leadingString ){
		
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
	public HttpRequesterBuilder continueIfTrue( final boolean condition ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform(TransformerContext ctx) {
				
				ctx.shouldContinue(condition);
			}
		});		
	}
	
	public HttpRequesterBuilder skipNextIfTrue( final boolean condition ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform(TransformerContext ctx) {
				
				ctx.skipNext(condition);
			}
		});
	}
	
	public HttpRequesterBuilder addTransformer( ITransformer transformer ){
		
		transformers.add(transformer);
		return this;
	}	
	
	public HttpRequester build() throws MalformedURLException{
		
		if( requester == null ){
			if( endpoint == null )
				throw new IllegalStateException("endpoint not set");			
			requester = new HttpRequester(endpoint);
		}else if( endpoint != null )
			requester.setEndpoint(endpoint);
		else if( requester.getEndpoint() == null )
			throw new IllegalStateException("endpoint not set");								
		
		requester.addTransformers(transformers);
		return requester;
	}

}
