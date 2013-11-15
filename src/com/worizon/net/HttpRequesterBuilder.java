package com.worizon.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import com.worizon.net.HttpRequester.ITransformer;
import com.worizon.net.HttpRequester.TransformerContext;

/**
 * Builder pattern to construct HttpRequester objects.
 */
final public class HttpRequesterBuilder {
	
	private  HttpRequester requester;
	private String endpoint;
	private  List<ITransformer> transformers = new LinkedList<ITransformer>();
		
	
	public HttpRequesterBuilder( HttpRequester requester ){
		
		this.requester = requester;		
	}
	
	public HttpRequesterBuilder(){
				
		/*URI uri = new URI(
    	"http", 
    	"search.barnesandnoble.com", 
    	"/booksearch/first book.pdf",
    	null);
		URL url = uri.toURL();*/
	}
	
	/**
	 * Set the endpoint for the HttpRequester object.
	 */
	public HttpRequesterBuilder endpoint( String endpoint ){
		
		this.endpoint = endpoint;
		return this;
	}
	
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
	
	public HttpRequesterBuilder bodyTrim(){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform(TransformerContext ctx){
				
				ctx.setBody( ctx.getBody().trim() );
			}
		});
	}
	
	public HttpRequesterBuilder bodyConcat( final String trailingString ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform( TransformerContext ctx ) {
				
				String body = ctx.getBody();
				ctx.setBody( body.concat( trailingString ) );
				
			}
		});		
	}
	
	public HttpRequesterBuilder bodyPrepend( final String leadingString ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform( TransformerContext ctx ) {
				
				String body = ctx.getBody();
				ctx.setBody( leadingString.concat(body) );
				
			}
		});		
	}
	
	
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
	
	public HttpRequester build(){
		
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
