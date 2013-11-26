package com.worizon.junit.jsonresponse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ JsonRpcDoubleResponseTest.class,
				JsonRpcDoubleArrayResponseTest.class,
				JsonRpcDoubleListResponseTest.class,
				JsonRpcFloatResponseTest.class,
				JsonRpcFloatArrayResponseTest.class,
				JsonRpcIntegerResponseTest.class,
				JsonRpcIntegerArrayResponseTest.class,
				JsonRpcStringResponseTest.class,
				JsonRpcStringArrayResponseTest.class,
				JsonRpcLongResponseTest.class,
				JsonRpcLongArrayResponseTest.class,
				JsonRpcObjectResponseTest.class,
				JsonRpcObjectArrayResponseTest.class,
				JsonRpcBooleanResponseTest.class,
				JsonRpcBooleanArrayResponseTest.class,
				JsonRpcVoidResponseTest.class,
				JsonRpcVoidArrayResponseTest.class,
				JsonRpcErrorResponseTest.class,				
				JsonRpcMixedTypesArrayResponseTest.class,
				JsonRpcMapResponseTest.class})
public class JsonRpcResponseSuite {

}
