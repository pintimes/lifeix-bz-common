package com.lifeix.bz.common.module.like.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lifeix.bz.common.module.like.po.TargetLikedPO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class TargetLikedDao {

	@Autowired
	private MongoTemplate template;

	public void increaseLike(String id) {
		increaseLike("likeNum", id);
	}

	public void increaseUnLike(String id) {
		increaseLike("unlikeNum", id);
	}

	public List<TargetLikedPO> findByIds(List<String> ids) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").in(ids));
		return template.find(query, TargetLikedPO.class);
	}

	public TargetLikedPO findById(String id) {
		return template.findById(id, TargetLikedPO.class);
	}

	private void increaseLike(String name, String id) {
		String collectionName = template.getCollectionName(TargetLikedPO.class);
		DBCollection collection = template.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		DBObject update = new BasicDBObject();
		update.put("$inc", new BasicDBObject(name, 1));
		boolean upsert = true;
		collection.findAndModify(query, null, null, false, update, false, upsert);
	}
}
