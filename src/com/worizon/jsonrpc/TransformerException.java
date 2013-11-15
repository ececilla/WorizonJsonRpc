package com.worizon.jsonrpc;

/**
 * Exception generated when a transformer throws an exception. The exception thrown will be set as the  
 * cause of the TransformerException.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class TransformerException extends RuntimeException {
	
	public TransformerException(){
		
	}
	
	public TransformerException( String message ){
		
		super(message);
	}
	
	public TransformerException( Throwable t){
		
		super(t);
	}
	
	public TransformerException( String message, Throwable t ){
		
		super(message,t);
	}

}
