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
	
	public static interface Listener{
		
		public void newLine( String data );
	};
	
	private String endpoint;
	private int nretries = 2;
	private final int DEFAULT_READ_TIMEOUT = 15000;
	private final int DEFAULT_CONNECT_TIMEOUT = 10000;
	private final static String SECRET  = "gritxt123";
	private final static String AGENT_VERSION  = "0.1.0";
	private HttpURLConnection conn = null;
		
	
	public HttpRequester( String endpoint ){
		
		this.endpoint = endpoint;
		
	}
	
	public void disconnect(){
		
		conn.disconnect();
		conn = null;
	}	
		
	public void sendAsyncPostRequest( String body, Listener listener ) throws MalformedURLException, IOException{
				
		try{ 
			System.out.println("retries: " + nretries);
			if(conn != null)
				disconnect();
			
			readLine( connect(body, 0), listener );
			
		}catch(IOException ex){
			
			if(nretries-- > 0)
				sendAsyncPostRequest(body, listener);
			else
				throw ex;
			
		}
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
		String encodedBody = URLEncoder.encode(body,"UTF-8");
		String encodedVersion = URLEncoder.encode(AGENT_VERSION,"UTF-8");
		String postData = "request=" + encodedBody + "&version=" + encodedVersion;		
		String key = "";
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(new String(postData + SECRET).getBytes("UTF-8"));			
			key = Base64.encodeToString(md5.digest(), Base64.NO_WRAP);		
		}catch(NoSuchAlgorithmException nsa){}
		
		conn.setDoOutput( true );
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
		conn.setReadTimeout(readTimeout);
		conn.setRequestMethod("POST");		
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "key=" + key);				 					
		conn.connect();		
		
		OutputStreamWriter writer = new OutputStreamWriter( conn.getOutputStream() );		
		writer.write( postData );		    													    
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
	
	private void readLine( InputStream is, Listener listener ) throws IOException {
		
		BufferedReader in  =  new BufferedReader(new InputStreamReader( is ));		
		String line;		
		while ((line = in.readLine()) != null) {   
							    					    			
			listener.newLine( line );			
		    
		}
	}

}
