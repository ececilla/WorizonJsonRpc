package com.worizon.junit;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.worizon.junit.jsonresponse.JsonRpcResponseSuite;

@RunWith(Suite.class)
@SuiteClasses({ JsonRpcResponseSuite.class })
public class AllTests {

}
