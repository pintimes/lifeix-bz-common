package com.lifeix.bz.common.module.comment.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.lifeix.bz.common.module.comment.po.CommentPO;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class CommentDao extends CommonDao<CommentPO>{

	public void delete(String userId, String commentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(commentId));
		query.addCriteria(Criteria.where("fromUserId").is(userId));
		template.remove(query, CommentPO.class);
	}

	public List<CommentPO> find(String subjectType, String subjectId, int limit, Date createTime) {
		Query query = new Query();
		query.addCriteria(Criteria.where("subjectType").is(subjectType));
		query.addCriteria(Criteria.where("subjectId").is(subjectId));
		query.addCriteria(Criteria.where("createTime").lt(createTime));
		query.limit(limit);
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		query.with(sort);
		return template.find(query, CommentPO.class);
	}

	public Map<String, Long> count(String subjectType, List<String> subjectIds) {
		BasicDBObject query = new BasicDBObject("subjectType", subjectType).append("subjectId", new BasicDBObject("$in", subjectIds));
		DBObject match = new BasicDBObject("$match", query);
		DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", new BasicDBObject("subjectId", "$subjectId")).append("count", new BasicDBObject("$sum", 1)));
		List<DBObject> pipeline = new ArrayList<>();
		pipeline.add(match);
		pipeline.add(group);
		DBCollection collection = template.getCollection(template.getCollectionName(CommentPO.class));
		AggregationOutput aggregate = collection.aggregate(pipeline);
		Iterable<DBObject> results = aggregate.results();
		// [ { "_id" : { "id" : "5850f8a10b236a8762813323"} , "count" : 1}]
		/**
		 * 组装
		 */
		Map<String, Long> map = new HashMap<>();
		for (DBObject dbObject : results) {
			DBObject data = (DBObject) dbObject.get("_id");
			String id = (String) data.get("subjectId");
			int count = (int) dbObject.get("count");
			map.put(id, new Long(count));
		}
		return map;
	}

	/**
	 * https://docs.mongodb.com/manual/core/map-reduce/ 查询多条Subject的评论
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月15日下午3:10:03
	 *
	 * @param subjectType
	 * @param subjectIds
	 * @param commentLimit
	 * @return
	 */
	public Map<String, List<CommentPO>> findSubjectComments(String subjectType, List<String> subjectIds, int commentLimit) {
		if (CollectionUtils.isEmpty(subjectIds)) {
			return null;
		}
		// TODO 先通过遍历的方式查询，发布之后再优化
		Map<String, List<CommentPO>> map = new HashMap<>();
		for (String subjectId : subjectIds) {
			List<CommentPO> cms = find(subjectType, subjectId, commentLimit, new Date());
			map.put(subjectId, cms);
		}
		return map;
	}
	
	public void increaseReplysum(String commentId) {
		Update update = new Update();
		update.inc("replySum", 1);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(commentId));
		template.updateFirst(query, update, CommentPO.class);
	}
	
	public void decreaseReplysum(String commentId) {
		Update update = new Update();
		update.inc("replySum", -1);
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(commentId));
		query.addCriteria(Criteria.where("replySum").gte(1));
		template.updateFirst(query, update, CommentPO.class);
	}

	//////////////// Like处理///////////////////

	public void increaseLikenum(String id) {
		Update update = new Update();
		update.inc("likeNum", 1);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		template.updateFirst(query, update, CommentPO.class);
	}

	public void decreaseLikenum(String id) {
		Update update = new Update();
		update.inc("likeNum", -1);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("likeNum").gte(1));
		template.updateFirst(query, update, CommentPO.class);
	}

	@Override
	public Class<CommentPO> getClassname() {
		return CommentPO.class;
	}

	

}
