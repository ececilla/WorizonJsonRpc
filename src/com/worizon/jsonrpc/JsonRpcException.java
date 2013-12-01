package com.worizon.jsonrpc;

/**
 * Base class for JSON-RPC exceptions, the error object returned from the server is bundled into this exception object.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class JsonRpcException extends RuntimeException {
	
		private JsonRpcError error = null;
	
		public JsonRpcException(){
			
			super();
		}
		
		public JsonRpcException( JsonRpcError error ){
			
			super(error.toString());
			this.error = error;
		}
		
		public JsonRpcException( String message ){
			
			super( message );
		}
		
		public JsonRpcException( String message, Throwable cause ){
			
			super( message, cause );
		}
		
		public JsonRpcException( Throwable cause ){
			
			super( cause );
		}
		
		public int getCode(){
			
			if( error != null)
				return error.getCode();
			else
				throw new IllegalStateException("No JSON-RPC error object setted.");
		}
		
		@Override
		public String getMessage(){
			
			if(error != null)
				return error.getMessage();
			else
				throw new IllegalStateException("No JSON-RPC error object setted.");
		}		
		
}
