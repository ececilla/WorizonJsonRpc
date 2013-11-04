package com.worizon.junit;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.worizon.junit.jsonrequest.JsonRpcRequestTest;
import com.worizon.junit.jsonresponse.JsonRpcResponseSuite;
import com.worizon.junit.rpc.HttpRequesterBuilderTest;
import com.worizon.junit.rpc.HttpRequesterTest;
import com.worizon.junit.rpc.RpcProxyTest;

@RunWith(Suite.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@SuiteClasses({ JsonRpcResponseSuite.class,
				JsonRpcRequestTest.class,
				RpcProxyTest.class,
				HttpRequesterTest.class,
				HttpRequesterBuilderTest.class})
public class AllTests {

}
