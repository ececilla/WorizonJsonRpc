package com.worizon.jsonrpc;

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
				return super.getMessage();
		}		
		
}
