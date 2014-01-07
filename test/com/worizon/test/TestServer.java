	package com.worizon.test;
	
	import java.io.BufferedReader;
	import java.io.IOException;
	import java.io.InputStream;
	import java.io.InputStreamReader;
	import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
	import java.util.LinkedHashMap;
	import java.util.Map;
	import java.util.Map.Entry;
	
	import org.aopalliance.intercept.MethodInterceptor;
	import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
	
	/**
	 *  Single-threaded server to test Http requester classes when doing unit testing. Unlike a mocked-based approach this class lets you test 
	 *  client http code with a real connection. So, the server accepts the incoming connection, reads http headers and body requests and closes 
	 *  the connection. All Http headers and body sent by the client under test can be inspectionated and checked with asserts in JUnit.
	 *  There are two ways of integrating this server into your testing plan, via an adapter method or via a proxy method.
	 *  
	 *  This class depends on spring-aop.jar, spring-core.jar, aopalliance.jar, commons-logging.jar
	 *  Tested with spring-aop-3.2.4.RELEASE.jar, spring-core-3.2.4.RELEASE.jar aopalliance-1.0.jar and commons-logging-1.1.3.jar 
	 *  
	 *  <p>Proxy 'request' method from your MyHttpRequester class, ex:
	 *  <pre>
	 *  	//server started and accepting connections on localhost:4444
	 *  	TestServer server = new TestServer(4444);
	 *  
	 *  	//proxy 'request' method on your class under test
	 *  	MyHttpRequester http = (MyHttpRequester)server.createTestRequester(new MyHttpRequester(), "request");
	 *  
	 *  	//set endpoint to localhost:port/whatever
	 *  	http.setEndpoint("http://localhost:4444/rpc");
	 *  
	 *  	//perform request to the server, the server closes the connection after the whole request is read an silences 
	 *  	//exceptions, to avoid try/catch code pollution in your test, thrown by HttpURLConnection since it was expecting a response back.  
	 *  	http.request("test body"); 
	 *  
	 *  	//test for headers and body sent with regular junit asserts
	 *  	assertEquals("test body", server.getBody());
	 *  	assertEquals("application/json", server.getHeaders().get("Content-Type"));
	 *  	...
	 *  </pre>
	 *  
	 *  <p>Adapt 'request' method from your MyHttpRequester class
	 *  
	 *  <pre>
	 *  	TestServer server = new TestServer(4444);
	 *  
	 *  	//adapt request and endpoint to your class under test.
	 *  	server.setAdapter(new TestServer.IRequester() {
	 *			
	 *			private MyHttpRequester myRequester = new MyHttpRequester();
	 *			
	 *			public void setEndpoint(String endpoint) {
	 *				
	 *				myRequester.setEndpoint(endpoint);//my particular method to set the endpoint
	 *			}
	 *			
	 *			public void doRequest(String body) throws Exception {
	 *				
	 *				myRequester.request(body);//my particular method to make the request.
	 *			} 
	 *		});
	 *
	 * 		TestServer.IRequester http = server.createTestRequester("http://localhost:4444/rpc");
	 *  	http.doRequest("my test body");
	 *  	assertEquals("my test body", server.getBody());
	 *  	assertEquals("application/json", server.getHeaders().get("Content-Type"));
	 *  	...  
	 *  </pre>
	 *  
	 *  @author Enric Cecilla
	 */
	public class TestServer extends BaseServer {
			
		private Map<String,String> headers = new LinkedHashMap<String,String>();
		private IRequester adapter;
		private String body;
		private String command;
		private int idleTime;			
		private IMapper mapper = new IMapper() {
			
			@Override
			public Object map(String obj) {
				
				return obj;
			}
		};
		
		/**
		 * Constructor
		 */
		public TestServer( int port ) throws IOException {
					
			super( port );
			start();
		}		
		
		/**
		 * Sets the idle time the server will take to read the requests. This idle time is used to test readtimeouts. 
		 */
		public void setIdleTime( int idleTime ){
			
			this.idleTime = idleTime;
		}			
		
		/**
		 * Gets the request's body payload sent.
		 */
		public String getBody(){
			
			return body;
		}
		
		public void setBodyMapper( IMapper mapper ){
			
			this.mapper = mapper;
		}
		
		/**
		 * Transform body into object with a string-to-object mapper.
		 */
		public Object getBodyAsObject(){
			
			return mapper.map(body);
		}
		
		/**
		 * Gets the request's command sent.
		 */
		public String getCommand(){
			
			return command;
		}
		
		/**
		 * Prints request's headers sent.
		 */
		public void dumpHeaders(){
			
			for( Entry<String,String> entry: getHeaders().entrySet()){
				
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
		}
		
		/**
		 * Gets request's headers sent.
		 */
		public Map<String,String> getHeaders(){
			
			return headers;
		}
		
		/**
		 * Sets the requester adapter for this server. 
		 */	
		public void setAdapter( IRequester adapter ){
			
			this.adapter = adapter;
		}					
					
		/**
		 * Builds the wrapped version of the adapter to make the request.
		 */
		public IRequester createTestRequester(String endpoint) throws MalformedURLException{
			
			return this.new NonResponseReadRequester(endpoint);
		}
		
		/**
		 * Builds the wrapped version of the adapter to make the request.
		 */
		public IRequester createTestRequester(){
			
			return this.new NonResponseReadRequester();
		}
			
		
		/**
		 * Builds proxied version of the requester and intercept requestMethod to mute network errors.
		 */
		public Object createTestRequester(Object obj, final String requestMethod){
			
			ProxyFactory pf = new ProxyFactory(obj);
			pf.addAdvice(new MethodInterceptor() {
				
				@Override
				public Object invoke(MethodInvocation mi) throws Throwable {
					
					Object retval = null;
					if( mi.getMethod().getName().equals(requestMethod) ){					
						try{
							
							String body = (String)mi.getArguments()[0];//body is expected as first parameter
							if(!body.endsWith("\n")){//make request, make body end with \n if it doesn't since the server reads lines ended with \n or \r or \n\r
								body += "\n";
								mi.getArguments()[0] = body;
							}
							mi.proceed();//make request
						
						}catch(ConnectException ex){//Silence ConnectException
						}
						try{ join(); }catch(InterruptedException ie){}
					}else
						retval = mi.proceed();//for other methods on object under test do nothing
					return retval;
				}
			});		
			
			return pf.getProxy();
		}
		
		/**
		 * Silences connection errors.
		 * @see com.worizon.test.BaseServer#handleException(java.lang.Exception)
		 */
		@Override
		protected void handleException( Exception ex ){
			
		}			
			
		/**
		 * Reads the request and stores the different parts.
		 * @see com.worizon.test.BaseServer#readStream(java.io.InputStream)
		 */
		@Override
		protected  void readStream( InputStream is ) throws IOException {
					
			try{//fake a readtimeout on the client if necessary
				sleep(idleTime);
			}catch(InterruptedException ie){}
			
			int numLines = 0;
			String inputLine;
			BufferedReader in  =  new BufferedReader(new InputStreamReader( is ));							
			while ((inputLine = in.readLine()) != null) {   									
				
				if(++numLines == 1){//request first line
					command = inputLine;
				}else{
					if( inputLine.isEmpty() ){//end of headers, read body line					    	
				    	
						body = in.readLine();				
						in.close();
						finish();
						break;
				    }else{//headers lines.			    	
				    	
				    	int colonSeparatorIndex = inputLine.indexOf(':');
						String key = inputLine.substring(0, colonSeparatorIndex).trim();
						String value = inputLine.substring(colonSeparatorIndex+1).trim();
						headers.put(key, value);					
				    }
				}
			}					
		}
		
		public interface IMapper{
			
			public Object map( String str );
		}
		
		/**
		 * Adapter interface
		 */
		public interface IRequester{
			
			public void setEndpoint(String endpoint) throws MalformedURLException;
			public void doRequest(String body) throws Exception;
		}
		
		/**
		 * The adapter object is wrapped with an object of this class. This class is responsible 
		 * for making the request through the adapter and silence the read stream exceptions because 
		 * of a premature connection close at the server.
		 * 
		 */
		private class NonResponseReadRequester implements IRequester{
			
			public NonResponseReadRequester(){}
			
			public NonResponseReadRequester(String endpoint) throws MalformedURLException{
				
				if(adapter == null)
					throw new IllegalStateException("Request adapter not set");
				
				adapter.setEndpoint(endpoint);
			}	
					
			public void setEndpoint( String endpoint) throws MalformedURLException{
				
				adapter.setEndpoint(endpoint);
			}
			
			public void doRequest( String body ){
				
				try{
					if( !body.endsWith("\n") )
						body += "\n";					
					adapter.doRequest(body); 
				}catch(Exception ex){}
				finally{//leave method and make sure server thread has joined with test thread.
					
					try{TestServer.this.join();}catch(InterruptedException ie){}				
				}						
			}						
					
		}
		
	}
		
