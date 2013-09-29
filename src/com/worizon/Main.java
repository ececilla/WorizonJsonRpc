package com.worizon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.worizon.jsonrpc.JsonRpcRequest;
import com.worizon.jsonrpc.JsonRpcResponse;
import com.worizon.net.HttpRequester;

public class Main {

	/**
	 * @param args
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public static void main(String[] args) throws Exception {
		//peta el servei
		/*
		EventService es = new EventService("http://localhost:8080/events",620793114);//Events for 620793114s			
		es.addObserver(new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				
				JsonRpcEvent ev = (JsonRpcEvent)arg;
				System.out.println(ev.toString());
			}
		});
		es.start();
		*/
		/*												
		//enviamos la request	
		JsonRpcRequest req = new JsonRpcRequest("myfunc");
		req.addParam("test", 1);
		req.addParam("dummy", "foooo");
		System.out.println( "req:" +  req );
		
	
		//Nos llega la primera respuesta		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": {\"x\":45,\"y\":67,\"s\":1.45}, \"id\": 2}";		
		JsonRpcResponse<B> response = new JsonRpcResponse<B>(message,B.class);		
		B b = response.getResult();
		System.out.println(b);
		
		//Nos llega la segunda respuesta
		String message2 = "{\"jsonrpc\": \"2.0\", \"result\": 67, \"id\": 2}";		
		JsonRpcResponse<Integer> response2 = new JsonRpcResponse<Integer>(message2,Integer.class);		
		int b2 = response2.getResult();
		System.out.println(b2);
				
		//Nos llega una tercera respuesta con error
		String message3 = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\":-5,\"message\":\"Error!\"}, \"id\": 2}";		
		JsonRpcResponse<Integer> response3 = new JsonRpcResponse<Integer>(message3,Integer.class);		
		Integer b3 = response3.getResult();
		System.out.println(b3);
		System.out.println(response3.getError());
		*/
		/*
		Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("test",5);
		
		Map<String, Object> params = new HashMap<String, Object>();
			params.put("doc",obj);
			params.put("uid",620793114L);
		
		//enviamos la request	
		
		JsonRpcRequest req = new JsonRpcRequest( "create",params );		
		System.out.println( "req:" +  req );
		
		
		RpcService rpc = new RpcService("http://localhost:8080/rpc");
		JsonRpcResponse<Dummy> response = rpc.sendSyncRequest( req, Dummy.class );
		System.out.println(response.getResult());
		*/
		/*
		DumpConsoleServer server = new DumpConsoleServer(4444);
		server.start();
		//Requester rq = new Requester("http://localhost:4444/api.php");
		//rq.sendRequest("this is a test");
		HttpRequester rq = new HttpRequester("http://localhost:4444/");
		String out = rq.sendPostRequest("{\"name\":\"test\"}\n");
		System.out.println( out );		
		server.finish();
			
		String json_str = "{\"ev_type\":\"ev_api_create\",\"ev_tstamp\":1352777351137,\"ev_data\":{\"test\":\"foooo!!\",\"uid\":444444444,\"catalog\":\"docs\",\"rcpts\":[444444444,620793117],\"ctime\":1352777351132,\"wid\":\"50a1be87dea0779d5c000015\"}}";
		JsonRpcEvent ev = new JsonRpcEvent(json_str, es);
		*/
		
		//AppService app = new AppService("http://192.168.1.4:8080/rpc");

		
		
		//unjoin call
		/*
		Map<String, Object> params3 = new HashMap<String, Object>();
			params3.put("wid","50a080ce70033dbb2a000003");
			params3.put("uid", 620793177);
		int result3 = app.unjoin(params3);
		System.out.println(result3);
		
		//join call
		
		Map<String, Object> params2 = new HashMap<String, Object>();
			params2.put("wid","50a080ce70033dbb2a000003");
			params2.put("uid", 620793177);
		Dummy dum2 = app.join(params2, Dummy.class);
		System.out.println(dum2);
		
		
		//create
		Map<String, Object> params0 = new HashMap<String, Object>();
			params0.put("uid",620793119);
			params0.put("doc", new Dummy(1000));
			params0.put("ttl",10);
		Dummy dum0 = app.create(params0, Dummy.class);
		System.out.println("create:"+ dum0);
		
		//get call
		Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("wid","50a1be87dea0779d5c000015");		
		Dummy dum1 = app.get(params1, Dummy.class);
		System.out.println("get:"+ dum1);
		
		//set call
		Map<String, Object> params4 = new HashMap<String, Object>();
			params4.put("wid","50a1be87dea0779d5c000015");
			params4.put("uid", 620793114);
			params4.put("fname","foo.bar");
			params4.put("value",new int[]{4,5,6});
		int result4 = app.set(params4);
		System.out.println("set:"+result4);
		
		//remove call
		Map<String, Object> params5 = new HashMap<String, Object>();
			params5.put("wid","50a1be87dea0779d5c000015");
			params5.put("uid", 620793114);
			params5.put("fname","foo.bar.1");		
		int result5 = app.remove(params5);
		System.out.println("remove:"+result5);
		
		//push call
		Map<String, Object> params6 = new HashMap<String, Object>();
			params6.put("wid","50a1be87dea0779d5c000015");
			params6.put("uid", 620793114);
			params6.put("fname","foo.bar");
			params6.put("value",99);
		int result6 = app.push(params6);
		System.out.println("push:"+result6);
		
		//pop call
		Map<String, Object> params7 = new HashMap<String, Object>();
			params7.put("wid","50a1be87dea0779d5c000015");
			params7.put("uid", 620793114);
			params7.put("fname","foo.bar");			
		int result7 = app.pop(params7);
		System.out.println("pop:" + result7);
		
		//shift call
		Map<String, Object> params8 = new HashMap<String, Object>();
			params8.put("wid","50a1be87dea0779d5c000015");
			params8.put("uid", 620793114);
			params8.put("fname","foo.bar");			
		int result8 = app.shift(params8);
		System.out.println("shift:" + result8);
		*/
		
		
		//Create
		/*
		Map<String, Object> params0 = new HashMap<String, Object>();		
			params0.put("uid", "50e44a96a5651f5625000001");
			params0.put("doc", new Dummy(500));
			params0.put("ttl", 60);
		Dummy dum0 = app.create(params0, Dummy.class);
		System.out.println("create:"+ dum0);
		*/
		//REgister
		
		/*
		User userhold = new User();
		userhold.email = "blabla@hotmail.com";
		userhold.name = "blabla";
		userhold.password  = "gritxt";
		Map<String, Object> params = new HashMap<String, Object>();										
		params.put("user", userhold);
				
		try{
			User user = app.register(params);
			System.out.println("register:" + user);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		*/
		JsonDeserializer<Boolean> deserializer = new JsonDeserializer<Boolean>(){
			
			public Boolean deserialize(JsonElement arg0, Type arg1,	JsonDeserializationContext arg2) throws JsonParseException {
				
				try{
					int value = arg0.getAsInt();
					if( value == 0)
						return false;
					else if( value == 1 )
						return true;
					else
						throw new JsonParseException("Boolean value " + value + " not valid");
							
				}catch(NumberFormatException nfe){
					String value = arg0.getAsString();
					if(value.toLowerCase().equals("true"))
						return true;
					else if( value.toLowerCase().equals("false") )
						return false;
					else
						throw new JsonParseException("Boolean value " + value + " not valid");							
				}				
			}
		};
		
		Gson gson = new GsonBuilder()
							.registerTypeAdapter(Boolean.class, deserializer)
							.registerTypeAdapter(boolean.class, deserializer)
							.create();
		B b = gson.fromJson("{xx:1,yy:3,f:1.2,b:1,s:\"jajajaj\"}", B.class);
		System.out.println("b:" + b);
	}

}
