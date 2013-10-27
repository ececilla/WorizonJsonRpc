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
	protected int idleTime;
	
	
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
	}
	
	public void setIdleTime( int idleTime ){
		
		this.idleTime = idleTime;
	}
	
	protected abstract void readStream( InputStream is ) throws IOException;
	
	@Override
	public void run(){
		try{														
			while( !isInterrupted() ){							
				clientSocket = serverSocket.accept();				
				if( clientSocket != null ){																										
					
					sleep(idleTime);
					readStream(  clientSocket.getInputStream()  );
				}
			}						        				
        }catch( SocketException se ){
        	        	
        }catch( InterruptedException ie){
        }catch(Exception e){
        	
        	e.printStackTrace();
        }
		
	}
}
