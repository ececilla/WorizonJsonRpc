package com.worizon.junit.gson;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worizon.jsonrpc.JsonRpcResponse;
import com.worizon.jsonrpc.gson.BooleanTypeAdapter;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class BooleanTypeAdapterTest {
	
	class Foo{
		boolean resultPri; 
		Boolean resultObj;
	}
	
	@Test
	public void testParseTrue() throws Exception{
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		String message = "{\"resultPri\":true,\"resultObj\":true}";
		Gson gson = builder.create();
		Foo b = (Foo)gson.fromJson(message, Foo.class);
		
		assertThat(b.resultPri, is(true));
		assertThat(b.resultObj, is(true));		
	}
	
	@Test
	public void testParseFalse() throws Exception{
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		String message = "{\"resultPri\":false,\"resultObj\":false}";
		Gson gson = builder.create();
		Foo b = (Foo)gson.fromJson(message, Foo.class);
						
		assertThat(b.resultPri, is(false));
		assertThat(b.resultObj, is(false));
	}
	
	@Test
	public void testParse1() throws Exception{
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		String message = "{\"resultPri\":1,\"resultObj\":1}";
		Gson gson = builder.create();
		Foo b = (Foo)gson.fromJson(message, Foo.class);
						
		assertThat(b.resultPri, is(true));
		assertThat(b.resultObj, is(true));
	}
	
	@Test
	public void testParse0() throws Exception{
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		String message = "{\"resultPri\":0,\"resultObj\":0}";
		Gson gson = builder.create();
		Foo b = (Foo)gson.fromJson(message, Foo.class);
						
		assertThat(b.resultPri, is(false));
		assertThat(b.resultObj, is(false));
	}
	
	@Test
	public void testParseWithSpaces() throws Exception{
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		String message = "{\"resultPri\":0,\"resultObj\":           0}";
		Gson gson = builder.create();
		Foo b = (Foo)gson.fromJson(message, Foo.class);
					
		assertThat(b.resultPri, is(false));
		assertThat(b.resultObj, is(false));
	}
	
	@Test
	public void testParseWithSpacesAndTabs() throws Exception{
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		String message = "{		\"resultPri\"	:	0,      \"resultObj\"    :						0}";
		Gson gson = builder.create();
		Foo b = (Foo)gson.fromJson(message, Foo.class);
						
		assertThat(b.resultPri, is(false));
		assertThat(b.resultObj, is(false));
	}

}
