package com.lifeix.bz.common.module.comment.dao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.lifeix.bz.common.module.comment.po.CLikePO;
import com.mongodb.WriteResult;

@Repository
public class CLikeDao {

	@Autowired
	private MongoTemplate template;

	public boolean save(String userId, String commentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("commentId").is(commentId));
		Update update = new Update();
		update.set("userId", userId);
		update.set("commentId", commentId);
		update.set("createTime", new Date());
		WriteResult upsert = template.upsert(query, update, CLikePO.class);
		return upsert.isUpdateOfExisting();
	}

	public int delete(String userId, String commentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("commentId").is(commentId));
		return template.remove(query, CLikePO.class).getN();
	}

	public List<String> findCommentIds(String userId, List<String> commentIds) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("commentId").in(commentIds));
		query.fields().include("commentId").exclude("_id");
		List<CLikePO> find = template.find(query, CLikePO.class);
		if (CollectionUtils.isEmpty(find)) {
			return null;
		}
		return find.stream().map(CLikePO::getCommentId).collect(Collectors.toList());
	}

}
