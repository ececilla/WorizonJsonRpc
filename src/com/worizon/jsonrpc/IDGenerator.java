package com.worizon.jsonrpc;

/**
 * Class responsible of generating ids for JSON-RPC requests.
 */
public class IDGenerator {

	private long id = 1;
	
	private static IDGenerator instance;
	
	private IDGenerator(){}
	
	public static IDGenerator getInstance(){
		
		if( instance == null ){
			instance = new IDGenerator();
		}
		return instance;
	}
	
	/**
	 * Creates an id.
	 * @return The id generated.
	 */
	public long createID(){
		
		return id++;
	}
}
