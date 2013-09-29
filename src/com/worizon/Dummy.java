package com.worizon;

import java.util.Arrays;


public class Dummy extends Doc{
		
	public int test;	
	
	public Dummy(int test){
		
		super(null,null);
		this.test = test;
	}
	
	public Dummy(String uid, String catalog, int test){
		
		super(uid,catalog);
		this.test = test;
	}
		
	@Override
	public String toString(){
		
		return "{wid:" + wid + ", uid:" + uid + ", catalog:" + catalog + ", rcpts:" + Arrays.toString(rcpts) + ", ctime:" + ctime + ", etime:" + etime + ", test:" + test + "}";
	}
	
}
