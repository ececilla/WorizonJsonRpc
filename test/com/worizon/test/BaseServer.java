package com.worizon.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 *  Single-threaded base tcp server with a backlog of 0 connections to process one request at a time.  
 *  Implement the abstract methods readStream and handleException to provide your particular implementation.
 *  Ex:
 *  
 *  public class MyServer extends BaseServer{...}
 *  
 *  MyServer server = new MyServer(4444);
 *  server.start();
 *  server.finish();
 */
public abstract class BaseServer extends Thread {
				
	protected ServerSocket serverSocket = null;
	protected Socket clientSocket  = null;	
	
	public BaseServer( int port ) throws IOException {
				
		serverSocket = new ServerSocket( port, 0 );		
	}
	
	public void start(){
		
		if(isAlive())
			return;
		else
			super.start();
	}
		
	
	public void finish() throws IOException {
						
		interrupt();		
		if( clientSocket != null && clientSocket.isConnected() )
			clientSocket.close();
		serverSocket.close();
		if(isAlive())
			try{ join(); }catch(InterruptedException ie){}
	}
	
	/**
	 * Provide a particular implementation of this method to suit your needs.
	 */
	protected abstract void readStream( InputStream is ) throws IOException;
	
	/**
	 * Provide a particular implementation of this method to suit your needs.
	 */
	protected abstract void handleException( Exception ex );
		
	
	/**
	 * Server loop, process one request at a time.
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run(){
		try{														
			while( !isInterrupted() ){
								
				clientSocket = serverSocket.accept();				
				if( clientSocket != null )																																				
					readStream(  clientSocket.getInputStream()  );				
			}						        				                	        	        
        }catch(Exception ex){
        	handleException(ex);        	
        }
		
	}
}
