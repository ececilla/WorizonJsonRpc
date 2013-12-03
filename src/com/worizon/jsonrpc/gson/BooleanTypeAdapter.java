package com.worizon.jsonrpc.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This TypeAdapter extends gson's default boolean type adapter. With this hook 
 * the library accepts both representations (1|0 and true|false) as valid boolean values.
 * 
 * @author Enric Cecilla
 * @since 1.0.0
 */
public class BooleanTypeAdapter implements JsonDeserializer<Boolean> {
	
	
	public Boolean deserialize(JsonElement arg0, Type arg1,	JsonDeserializationContext arg2) throws JsonParseException {
				
		if(arg0.isJsonPrimitive()){
			
			try{ //try if this supposed boolean is encoded as:"0"|"1"
				int value = arg0.getAsInt();				
				if( value == 0 )
					return false;
				else if( value == 1 )
					return true;
				else
					throw new JsonParseException("Boolean value " + arg0.toString() + " not valid");
			}catch(NumberFormatException nfe){//try if this supposed boolean is encoded as:"true"|"false"
				
				String value = arg0.getAsString();
				if(value.toLowerCase().equals("true"))
					return true;
				else if( value.toLowerCase().equals("false") )
					return false;
				else
					throw new JsonParseException("Boolean value " + arg0.toString() + " not valid");
			}
		}else
			throw new JsonParseException("Boolean value " + arg0.toString() + " not valid");	
	}
		
}
