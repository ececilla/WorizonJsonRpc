package com.worizon.junit.rpc;

import org.junit.Test;
import static org.junit.Assert.*;
import com.worizon.jsonrpc.TypesUtil;

public class TypesUtilTest {
	
	@Test
	public void testAllTrue(){
		
		Object objs[] = new Object[]{new Integer(1), new Integer(2), new Integer(3), new Integer(4)};
		assertTrue( TypesUtil.all(objs).haveType(Integer.class) );
		
	}
	
	@Test
	public void testAllFalse(){
		
		Object objs[] = new Object[]{new Integer(1), new Double(2), new Integer(3), new Integer(4)};
		assertFalse( TypesUtil.all(objs).haveType(Integer.class) );
		
	}
	
	@Test
	public void testAnyTrue(){
		
		Object objs[] = new Object[]{new Double(1), new Long(2), new Integer(3), new Float(4)};
		assertTrue( TypesUtil.any(objs).haveType(Integer.class) );
		
	}
	
	@Test
	public void testAnyFalse(){
		
		Object objs[] = new Object[]{new Double(1), new Long(2), new String(""), new Float(4)};
		assertFalse( TypesUtil.any(objs).haveType(Integer.class) );
		
	}
	
	@Test
	public void testNoneTrue(){
		
		Object objs[] = new Object[]{new Double(1), new Long(2), new Short((short)0), new Float(4)};
		assertTrue( TypesUtil.none(objs).haveType(Integer.class) );
		
	}
	
	@Test
	public void testNoneFalse(){
		
		Object objs[] = new Object[]{new Double(1), new Integer(2), new Short((short)0), new Float(4)};
		assertFalse( TypesUtil.none(objs).haveType(Integer.class) );
		
	}
	
	
}
