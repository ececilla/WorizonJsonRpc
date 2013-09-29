package com.worizon;

import com.google.gson.annotations.SerializedName;

public class B {
	
	@SerializedName("xx")
	private int x;
	
	@SerializedName("yy")
	private int y;
	
	private float f;
	private boolean b;
	private String s;
	
	public B(){}	
	
	@Override
	public String toString(){
		return "{x:" + x + ",y:" + y + ",f:" + f + ",b:" + b + ",s:" + s + "}"; 
	}

}
