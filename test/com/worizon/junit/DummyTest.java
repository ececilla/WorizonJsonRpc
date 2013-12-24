package com.worizon.junit;

import org.junit.Test;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class DummyTest {
	
	@Test
	public void otto(){
		
		Bus bus = new Bus(ThreadEnforcer.ANY);
		bus.register(new Object(){
			
			@Subscribe
			public void newEvent( Integer result ){
				
				System.out.println(result);
			}
		});
		bus.post(5);
		
	}

}
