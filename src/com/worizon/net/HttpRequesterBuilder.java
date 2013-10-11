package com.worizon.net;

import java.util.LinkedList;
import java.util.List;

import com.worizon.net.HttpRequester.ITransformer;
import com.worizon.net.HttpRequester.TransformerContext;

/**
 * 
 */
public class HttpRequesterBuilder {
	
	private  HttpRequester requester;
	private String endpoint;
	private  List<ITransformer> transformers = new LinkedList<ITransformer>();
	
	public HttpRequesterBuilder( String endpoint ){
		
		this.endpoint = endpoint; 
	}
	
	public HttpRequesterBuilder payloadURLEncoding(){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform( TransformerContext ctx ) {
				
				String payloadString = ctx.getPayload();				
				//DO URL encoding
				ctx.setPayload(payloadString);
			}
		});
		
	}
	
	public HttpRequesterBuilder payloadConcat( final String trailingString ){
		
		return addTransformer(new ITransformer() {
			
			@Override
			public void transform( TransformerContext ctx ) {
				
				String payload = ctx.getPayload();
				ctx.setPayload( payload.concat( trailingString ));
				
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
		
		requester = new HttpRequester(endpoint);
		requester.addTransformers(transformers);
		return requester;
	}

}
