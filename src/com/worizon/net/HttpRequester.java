package com.worizon.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpRequester {
			
	private String endpoint;
	private int nretries = 2;
	private final int DEFAULT_READ_TIMEOUT = 15000;
	private final int DEFAULT_CONNECT_TIMEOUT = 10000;	
	private HttpURLConnection conn = null;
	
	public HttpRequester(){}
	
	public HttpRequester( String endpoint ){
		
		this.endpoint = endpoint;
		
	}
	
	public void disconnect(){
		
		conn.disconnect();
		conn = null;
	}	
				
	public String sendSyncPostRequest( String body, int readTimeout ) throws MalformedURLException, IOException, InterruptedException{
							    
	    try{
	    	return readResponse( connect(body, readTimeout) );
	    }catch(IOException ex){
	    	
	    	disconnect();
	    	if( !Thread.interrupted() ){
		    	if(nretries-- > 0)
		    		return sendSyncPostRequest(body, readTimeout);
		    	else
		    		throw ex;
	    	}else
	    		throw new InterruptedException();
	    }
	}
	
	public String sendSyncPostRequest( String body ) throws MalformedURLException, IOException, InterruptedException{
				
		return sendSyncPostRequest(body, DEFAULT_READ_TIMEOUT);
	}
	
	private InputStream connect( String body, int readTimeout) throws MalformedURLException, IOException {
		
		URL url = new URL( endpoint );
		conn = (HttpURLConnection)url.openConnection();		
		
		conn.setDoOutput( true );
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
		conn.setReadTimeout(readTimeout);
		conn.setRequestMethod("POST");		
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");					 				
		conn.connect();		
		
		OutputStreamWriter writer = new OutputStreamWriter( conn.getOutputStream() );		
		writer.write( body );		    													    
	    writer.flush();
	    try{
	    	
	    	return conn.getInputStream();
	    
	    }catch(FileNotFoundException fex){
	    	
	    	return conn.getErrorStream();
	    }
	}
					
	private String readResponse( InputStream is ) throws IOException{
		
		BufferedReader in = new BufferedReader( new InputStreamReader( is ) );		 		
		StringBuffer buffer = new StringBuffer();
								
		int cdata;
		while( (cdata = in.read()) != -1 ){
			
			buffer.append((char) cdata );
		}
		//is.close();//should close inputstream to close connection.
		return buffer.toString();
		
	}	
		
}
