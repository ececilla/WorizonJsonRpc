package com.worizon.jsonrpc;

@SuppressWarnings("serial")
public class JsonRpcException extends Exception {
	
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
		
		
}
