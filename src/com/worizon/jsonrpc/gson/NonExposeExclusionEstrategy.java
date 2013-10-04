package com.worizon.jsonrpc.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class NonExposeExclusionEstrategy implements ExclusionStrategy {
	
	public boolean shouldSkipClass(Class<?> clazz) {
	      
		return false;
	}
	
	public boolean shouldSkipField(FieldAttributes f){
		
		return f.getAnnotation(NonExpose.class) != null;
	}

}
