package com.worizon.jsonrpc;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Class responsible of generating ids for JSON-RPC requests.
 */
public class IDGenerator {
	
	private AtomicLong id = new AtomicLong(0);
	
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
	public long incrementAndGet(){
		
		return id.incrementAndGet();
	}
	
	/**
	 * Resets the requests counter.
	 */
	public void reset(){
		
		id.set(0);
	}
}
