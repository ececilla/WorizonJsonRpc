package com.worizon;

public class A {
	
	public  class Sync{
		 public void a(){}
		 public void b(){}
	};
	
	public class Async{
		
		public void a(){}
		public void b(){}
	};
	
	public B b;
	public String z;
	
	public A(){
		
		Sync s = this.new Sync();
	}	

	
	@Override
	public String toString(){
		
		return "b:{" + b + "}," + "z:" + z;
	}

}
