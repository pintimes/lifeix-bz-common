package com.lifeix.test.bz.common.like;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.lifeix.football.common.util.OKHttpUtil;
import com.squareup.okhttp.Response;

public class LikeActionTest {
	
	
	@Test
	public void test() throws Exception{
		Map<String, Object> headers = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("type", "player");
		params.put("target", 20);
		params.put("like", true);
		Response response = OKHttpUtil.post("http://54.223.127.33:8000/football/like/likes?key=visitor", headers, params);
		if (!response.isSuccessful()) {
			System.out.println(response.message());
			Assert.fail();
		}
		System.out.println(JSONObject.toJSON(response));
	}

}
