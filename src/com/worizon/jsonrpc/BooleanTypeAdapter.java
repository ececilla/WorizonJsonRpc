package com.worizon.jsonrpc;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BooleanTypeAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

	public JsonElement serialize(Boolean arg0, Type arg1, JsonSerializationContext arg2) {
		
		return new JsonPrimitive( arg0?1:0);
	}
	
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
		
}
