package com.worizon.jsonrpc;

@SuppressWarnings("serial")
public class RemoteException extends RuntimeException {
	
		private JsonRpcError error = null;
	
		public RemoteException(){
			
			super();
		}
		
		public RemoteException( JsonRpcError error ){
			
			super(error.toString());
			this.error = error;
		}
		
		public RemoteException( String message ){
			
			super( message );
		}
		
		public RemoteException( String message, Throwable cause ){
			
			super( message, cause );
		}
		
		public RemoteException( Throwable cause ){
			
			super( cause );
		}
		
		public int getCode(){
			
			return error.getCode();
		}
		
		public String getMessage(){
			
			return error.getMessage();
		}
		
		
}
