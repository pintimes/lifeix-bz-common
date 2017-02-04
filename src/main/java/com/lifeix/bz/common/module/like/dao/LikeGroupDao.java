package com.lifeix.bz.common.module.like.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.lifeix.bz.common.module.like.po.LikeGroupPO;

@Repository
public class LikeGroupDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public void save(LikeGroupPO groupPO) {
		mongoTemplate.save(groupPO);
	}

	public LikeGroupPO findById(String id) {
		return mongoTemplate.findById(id, LikeGroupPO.class);
	}
	
	public List<LikeGroupPO> findAll(){
		return mongoTemplate.findAll(LikeGroupPO.class);
	}
}
