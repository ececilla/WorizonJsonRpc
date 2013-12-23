package com.worizon.jsonrpc;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Class responsible of generating ids for JSON-RPC requests.
 */
public class IDGenerator {
	
	/**
	 * Internal identifier counter enabled with atomic operations.
	 */
	private AtomicLong id = new AtomicLong(0);	
	
	/**
	 * singleton instance
	 */
	private static IDGenerator instance = new IDGenerator();
	
	/**
	 * No public construction.
	 */
	private IDGenerator(){}
	
	/**
	 * Gets instance of this generator.
	 * @return Singleton IDGenerator instance.  
	 */
	public static IDGenerator getInstance(){
				
		return instance;
	}
	
	/**
	 * Creates an id.
	 * @return The id generated.
	 */
	public long generate(){
		
		return id.incrementAndGet();
	}
	
	/**
	 * Resets the counter.
	 */
	public void reset(){
		
		id.set(0);
	}
}
