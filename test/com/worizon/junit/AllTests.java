package com.worizon.junit;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.worizon.junit.jsonrequest.JsonRpcRequestTest;
import com.worizon.junit.jsonresponse.JsonRpcResponseSuite;
import com.worizon.junit.rpc.HttpRequestBuilderTest;
import com.worizon.junit.rpc.HttpRequestTest;
import com.worizon.junit.rpc.RpcTest;

@RunWith(Suite.class)
@SuiteClasses({ JsonRpcResponseSuite.class,
				JsonRpcRequestTest.class,
				RpcTest.class,
				HttpRequestTest.class,
				HttpRequestBuilderTest.class})
public class AllTests {

}
