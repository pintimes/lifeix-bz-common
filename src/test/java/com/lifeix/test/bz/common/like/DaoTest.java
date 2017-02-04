package com.lifeix.test.bz.common.like;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lifeix.bz.common.module.like.dao.LikeGroupDao;
import com.lifeix.bz.common.module.like.dao.TargetLikedDao;
import com.lifeix.bz.common.module.like.po.LikeGroupPO;
import com.lifeix.test.bz.common.BaseTest;

public class DaoTest extends BaseTest{

	@Autowired
	LikeGroupDao likeGroupDao;
	
	@Autowired
	TargetLikedDao targetLikedDao ;
	
	@Test
	public void test(){
		LikeGroupPO groupPO = new LikeGroupPO();
		groupPO.setTargets(Arrays.asList("12","2121"));
		likeGroupDao.save(groupPO);
		
		targetLikedDao.increaseLike("2");
	}
	
}
