package com.lifeix.test.bz.common;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.lifeix.football.common.util.OKHttpUtil;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

public class NormalTest {
	
	@Test
	public void test() throws Exception{
		String host = "http://54.223.127.33:8000";
		String qrUrl =  host+"/football/like/qr?link=https://www.c-f.com/news/detail/5897bd94e4b0af2530fbbd85&key=visitor";
		testGet(qrUrl);

		String appgatewayUrl = host+"/football/appgateway/decision/getDecisionItems?categoryId=2016-csl&roundId=2016-csl-29&key=visitor";
		testGet(appgatewayUrl);
		
		String commentUrl = host+"/football/comment/replies?key=visitor&commentId=588169b1e4b02e86288cd47b";
		testGet(commentUrl);
		
		String likeUrl = host+"/football/like/likes/1?type=coach&key=visitor";
		testGet(likeUrl);
		
		String likeGroupUrl = host+"/football/like/likes?group=player%E4%B8%AD%E5%9B%BD%E7%94%B7%E8%B6%B3%E5%9B%BD%E5%AE%B6%E9%98%9F&key=visitor";
		testGet(likeGroupUrl);
		
		String messageUrl = host+"/football/message/messages/count?key=bad507e293114dbeb76e6b3ec48ad4e8&app=competition";
		testGet(messageUrl);
	}
	
	public void testProd(){
//		https://api.c-f.com/football/message/messages/count?key=a6f815fdbbdf4e8cabc3be121d8a2774&app=competition
	}
	
	private void testGet(String url) throws Exception{
		Response response = OKHttpUtil.get(url, null);
		if (!response.isSuccessful()) {
			Assert.fail();
		}
		ResponseBody body = response.body();
		byte[] bytes = body.bytes();
		String result = new String(bytes);
		if (StringUtils.isEmpty(result)) {
			Assert.fail();
		}
		System.out.println(result);
	}

}
