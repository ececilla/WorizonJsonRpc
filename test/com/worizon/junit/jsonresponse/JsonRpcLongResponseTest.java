package com.worizon.junit.jsonresponse;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.worizon.jsonrpc.JsonRpcResponse;

public class JsonRpcLongResponseTest {
	

	@Test
	public void testResult() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": 3487394782749223234, \"id\": 2}";		
		JsonRpcResponse<Long> res = new JsonRpcResponse<Long>(message, Long.class);
				
		assertThat( res.getResult(), is(3487394782749223234L)  );
		assertThat( res.getId(), is(2L) );				
		assertThat(res.getVersion(), is(equalTo("2.0")));
		assertThat(res.getError(), is(nullValue()));
		
		JsonRpcResponse<Long> res2 = new JsonRpcResponse<Long>(message, long.class);
		assertThat( res2.getResult(), is(3487394782749223234L)  );
	}
	
	@Test
	public void testNull() throws Exception{
		
		String message = "{\"jsonrpc\": \"2.0\", \"result\": null, \"id\": 2}";		
		JsonRpcResponse<Long> res = new JsonRpcResponse<Long>(message, Long.class);
		
		assertThat(res.getResult(), is(nullValue()));
		
	}
	
}
