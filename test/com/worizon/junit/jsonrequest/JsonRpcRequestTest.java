package com.worizon.junit.jsonrequest;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.worizon.jsonrpc.IDGenerator;
import com.worizon.jsonrpc.JsonRpcRequest;
import com.worizon.jsonrpc.gson.NonExpose;

public class JsonRpcRequestTest {

	@Before
	public void setUp() {

		IDGenerator.getInstance().reset();
	}

	@Test
	public void testId() {

		for (long i = 1; i < 10000; i++) {

			JsonRpcRequest req = new JsonRpcRequest("test");
			assertThat(i, is(equalTo(req.getId().longValue())));
		}
	}

	@Test
	public void testConstructor1() {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		JsonRpcRequest request = new JsonRpcRequest("test", params);

		assertThat(request.getId(), is(notNullValue()));
		assertThat(request.getVersion(), is(equalTo("2.0")));
		assertThat(request.getMethod(), is(equalTo("test")));

	}

	@Test
	public void testConstructor2() {

		JsonRpcRequest request = new JsonRpcRequest("test");

		assertThat(request.getId(), is(notNullValue()));
		assertThat(request.getVersion(), is(equalTo("2.0")));
		assertThat(request.getMethod(), is(equalTo("test")));
		assertThat(request.toString(), is(equalTo("{\"method\":\"test\",\"jsonrpc\":\"2.0\",\"id\":1}")));
	}

	@Test
	public void testConstructor3() {

		JsonRpcRequest request1 = new JsonRpcRequest("test");
		JsonRpcRequest request2 = new JsonRpcRequest(request1);

		assertThat(request1, is(equalTo(request2)));
	}

	@Test
	public void testEquals() {

		JsonRpcRequest req1 = new JsonRpcRequest("test");
		JsonRpcRequest req2 = new JsonRpcRequest("test");

		assertThat(req1, is(not(equalTo(req2))));
	}

	@Test
	public void testParamsOrderToString() {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("y", new int[] { 3, 4, 5 });
		params.put("x", 1);
		params.put("z", true);
		JsonRpcRequest request = new JsonRpcRequest("test", params);
		request.setId(1000L);

		assertThat(
				request.toString(),
				is(equalTo("{\"method\":\"test\",\"params\":{\"y\":[3,4,5],\"x\":1,\"z\":true},\"jsonrpc\":\"2.0\",\"id\":1000}"))
				);

	}

	class A {
		int x;
		String y;

		public A(int x, String y) {

			this.x = x;
			this.y = y;
		}
	}

	@Test
	public void testObjectToString() {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("a", new A(5, "test"));
		JsonRpcRequest request = new JsonRpcRequest("test", params);
		request.setId(1000L);

		assertThat(
				request.toString(),
				is(equalTo("{\"method\":\"test\",\"params\":{\"a\":{\"x\":5,\"y\":\"test\"}},\"jsonrpc\":\"2.0\",\"id\":1000}"))
				);
	}

	class B {
		int x;
		@NonExpose
		String y;

		public B(int x, String y) {

			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {

			return ((B) obj).x == x && ((B) obj).y.equals(y);
		}
	}

	@Test
	public void testObjectNonExposeFieldToString() {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("b", new B(5, "test"));
		JsonRpcRequest request = new JsonRpcRequest("test", params);		
		System.out.println(request.toString());
		assertThat(
				request.toString(),
				is(equalTo("{\"method\":\"test\",\"params\":{\"b\":{\"x\":5}},\"jsonrpc\":\"2.0\",\"id\":1}"))
				);
	}

	@Test
	public void testParseNumbers() {

		JsonRpcRequest req = JsonRpcRequest
								.parse("{\"method\":\"test_numbers\",\"params\":{\"x\":5,\"y\":10.0},\"jsonrpc\":\"2.0\",\"id\":1000}");

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("x", 5.0);
		params.put("y", 10.0);
		
		assertThat(params, is(equalTo(req.getParams())));
		assertThat(req.getId(), is(equalTo(1000L)));
		assertThat(req.getMethod(), is(equalTo("test_numbers")));
	}

	@Test
	public void testParseStrings() {

		JsonRpcRequest req = JsonRpcRequest
				.parse("{\"method\":\"test_strings\",\"params\":{\"x\":\"foo\",\"y\":\"bar\"},\"jsonrpc\":\"2.0\",\"id\":1000}");

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("x", "foo");
		params.put("y", "bar");
				
		assertThat( req.getId(), is(equalTo(1000L)) );
		assertThat( params,is(equalTo(req.getParams())) );
		assertThat( req.getMethod(), is(equalTo("test_strings")) );
	}

	@Test
	public void testParseArray() {

		JsonRpcRequest req = JsonRpcRequest
				.parse("{\"method\":\"test_array\",\"params\":{\"x\":[1,2,3]},\"jsonrpc\":\"2.0\",\"id\":1000}");

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		ArrayList<Double> values = new ArrayList<Double>(Arrays.asList(new Double[]{1d,2d,3d}));
		params.put("x", values);
		
		assertThat( params, is(equalTo(req.getParams())) );
		assertThat( req.getId(), is(equalTo(1000L)) );
		assertThat( req.getMethod(), is(equalTo("test_array")) );
				
	}

}
