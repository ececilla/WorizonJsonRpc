package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcObjectResponseTest {
	
	class Foo{
		
		int dni;
		String name;
		String surname;
		float height;
		
		public Foo(){}
		
		public Foo( int dni, String name, String surname, float height){
			
			this.dni = dni;
			this.name = name;
			this.surname = surname;
			this.height = height;
		}
		
		public boolean equals( Object obj ){
			Foo foo = (Foo)obj;
			return dni == foo.dni && name.equals(foo.name) && surname.equals(foo.surname) && height == foo.height;
		}
				
	}
	
	class Bar extends Foo{
		
		public Bar(){
			
		}
	}

	@Test
	public void testResult() throws Exception {
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": {\"dni\":46579878,\"name\":\"Enric\",\"surname\":\"Cecilla\",\"height\":1.72}, \"id\": 77684}";		
		JsonRpcResponse<Foo> res = new JsonRpcResponse<Foo>(message, Foo.class);		
						
		Foo expected = new Foo(46579878,"Enric","Cecilla",1.72f);
		
		assertThat( res.getResult(), is(expected));
		assertThat( res.getError(), is(nullValue()));		
		
	}
	
	@Test
	public void testResultExtendedObject() throws Exception {
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": {\"dni\":46579878,\"name\":\"Enric\",\"surname\":\"Cecilla\",\"height\":1.72}, \"id\": 77684}";		
		JsonRpcResponse<Foo> res = new JsonRpcResponse<Foo>(message, Bar.class);		
									
		Foo expected = new Foo(46579878,"Enric","Cecilla",1.72f);		
		assertThat( res.getResult(), is(expected));
		assertThat(res.getError(), is(nullValue()));	
		assertThat( res.getResult() instanceof Bar, is(true) );
		
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 77684}";		
		JsonRpcResponse<Foo> res = new JsonRpcResponse<Foo>(message, Foo.class);
		
		assertThat(res.getResult(), is(nullValue()));
	}
					
		
}
