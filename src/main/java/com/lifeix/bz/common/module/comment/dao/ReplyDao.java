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

import com.lifeix.bz.common.module.comment.po.ReplyPO;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class ReplyDao extends CommonDao<ReplyPO> {

	public void delete(String userId, String commentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(commentId));
		query.addCriteria(Criteria.where("fromUserId").is(userId));
		template.remove(query, ReplyPO.class);
	}
	
	public ReplyPO findAndRemove(String userId, String commentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(commentId));
		query.addCriteria(Criteria.where("fromUserId").is(userId));
		return template.findAndRemove(query, ReplyPO.class);
	}

	public List<ReplyPO> findRepliesByComment(String commentId, int limit, Date date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("commentId").is(commentId));
		query.addCriteria(Criteria.where("createTime").gt(date));
		Sort sort = new Sort(Sort.Direction.ASC, "createTime");
		query.with(sort);
		query.limit(limit);
		return template.find(query, ReplyPO.class);
	}

	public Map<String, Long> countCommentReplyNum(List<String> commentIds) {
		BasicDBObject query = new BasicDBObject("commentId", new BasicDBObject("$in", commentIds));
		DBObject match = new BasicDBObject("$match", query);
		DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", new BasicDBObject("id", "$commentId")).append("count", new BasicDBObject("$sum", 1)));
		List<DBObject> pipeline = new ArrayList<>();
		pipeline.add(match);
		pipeline.add(group);
		DBCollection collection = template.getCollection(template.getCollectionName(ReplyPO.class));
		AggregationOutput aggregate = collection.aggregate(pipeline);
		Iterable<DBObject> results = aggregate.results();
		// [ { "_id" : { "id" : "5850f8a10b236a8762813323"} , "count" : 1}]
		/**
		 * 组装
		 */
		Map<String, Long> map = new HashMap<>();
		for (DBObject dbObject : results) {
			DBObject data = (DBObject) dbObject.get("_id");
			String id = (String) data.get("id");
			int count = (int) dbObject.get("count");
			map.put(id, new Long(count));
		}
		return map;
	}

	/**
	 * 查询多条评论的回复
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月15日下午3:31:00
	 *
	 * @param commentIds
	 * @param replyLimit
	 * @return
	 */
	public Map<String, List<ReplyPO>> findReplies(List<String> commentIds, int replyLimit,Date date) {
		if (CollectionUtils.isEmpty(commentIds)) {
			return null;
		}
		// TODO 先通过遍历的方式查询，发布之后再优化
		Map<String, List<ReplyPO>> map = new HashMap<>();
		for (String commentId : commentIds) {
			List<ReplyPO> cms = findRepliesByComment(commentId, replyLimit, date);
			map.put(commentId, cms);
		}
		return map;
	}

	//////////////// Like处理///////////////////

	public void increaseLikenum(String id) {
		Update update = new Update();
		update.inc("likeNum", 1);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		template.updateFirst(query, update, ReplyPO.class);
	}

	public void decreaseLikenum(String id) {
		Update update = new Update();
		update.inc("likeNum", -1);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("likeNum").gte(1));
		template.updateFirst(query, update, ReplyPO.class);
	}

	@Override
	public Class<ReplyPO> getClassname() {
		return ReplyPO.class;
	}

}
