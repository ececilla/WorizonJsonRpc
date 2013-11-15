package com.worizon.jsonrpc;

/**
 * High level exception of a remote procedure call. Error codes specified at the JSONRPC spec are mapped as 
 * a JsonRpcException. Error codes different from the spec are mapped and thrown as a RemoteException.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
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
		
		@Override
		public String getMessage(){
			
			return error.getMessage();
		}		
		
}
