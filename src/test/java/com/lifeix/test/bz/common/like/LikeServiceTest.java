package com.lifeix.test.bz.common.like;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.lifeix.bz.common.module.like.model.Like;
import com.lifeix.bz.common.module.like.service.LikeService;
import com.lifeix.test.bz.common.BaseTest;

public class LikeServiceTest extends BaseTest{

	@Autowired
	private LikeService likeService;

	@Test
	public void test(){
//		String source = "app";
//		String type = "test";
//		String target = String.valueOf(System.currentTimeMillis());
//		boolean like = true;
//		Like likeResult1 = likeService.addLike(source, type, target, like);
//		System.out.println(JSONObject.toJSONString(likeResult1));
//		//
//		Like likeResult2 = likeService.getLike(source, type, target);
//		System.out.println(JSONObject.toJSONString(likeResult2));
		//
		String group = "nationalteam";
		List<Like> groupLikes = likeService.getGroupLikes(group, "app");
		System.out.println(JSONArray.toJSONString(groupLikes));
	}
	
}
