package com.worizon.junit.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


/**
 *  Servidor single-thread TCP para hacer dump de lo que mandan los clientes.
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
		if( clientSocket.isConnected() )
			clientSocket.close();
		serverSocket.close();			
	}
	
	protected abstract void readStream( InputStream is ) throws IOException;
	
	@Override
	public void run(){
		try{														
			while( !isInterrupted() ){
												
				clientSocket = serverSocket.accept();				
				if( clientSocket != null )																										
					readStream(  clientSocket.getInputStream()  );																								
			}						        				
        }catch( Exception e ){
        	
        	e.printStackTrace();
        }
		
	}
}
