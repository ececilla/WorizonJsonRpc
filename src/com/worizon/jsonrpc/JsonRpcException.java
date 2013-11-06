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
			
			return error.getCode();
		}
		
		@Override
		public String getMessage(){
			
			return error.getMessage();
		}		
		
}
