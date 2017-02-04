package com.lifeix.bz.common.module.like.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.lifeix.bz.common.module.like.po.LikePO;
import com.lifeix.football.common.util.MapUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class LikeDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * 清楚数据库
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年5月25日上午11:05:41
	 *
	 */
	public void clear() {
		mongoTemplate.remove(new Query(), LikePO.class);
	}

	/**
	 * 插入一个喜欢
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年5月25日上午10:49:31
	 *
	 * @param like
	 */
	public void save(LikePO like) {
		/**
		 * 如果like存在则不保存
		 */
		mongoTemplate.save(like);
	}
	
	public LikePO findById(String id) {
		return mongoTemplate.findById(id, LikePO.class);
	}

	public LikePO getSourceLikes(String type, String source, String target) {
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is(type).and("source").is(source).and("target").is(target));
		return mongoTemplate.findOne(query, LikePO.class);
	}
	
	public List<LikePO> findByIds(List<String> ids) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").in(ids));
		return mongoTemplate.find(query, LikePO.class);
	}

	public List<LikePO> getSourceLikes(String type, String source, List<String> targets) {
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is(type).and("source").is(source).and("target").in(targets));
		return mongoTemplate.find(query, LikePO.class);
	}

	/**
	 * 获得source的喜欢记录
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年5月25日上午10:49:16
	 *
	 * @param source
	 * @return
	 */
	public List<LikeRecord> getSourceLikes(String type, String source) {
		BasicDBObject query = new BasicDBObject("source", source).append("type", type);
		Iterable<DBObject> results = getAggregate(query);
		return transformLikeResult(results);
	}

	/**
	 * target 被喜欢的次数 ，每一个Like是一个Target的like记录
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年5月25日上午10:47:23
	 *
	 * @param targets
	 * @return
	 */
	public List<LikeRecord> getTargetsLiked(String type, List<String> targets) {
		BasicDBObject query = new BasicDBObject("type", type);
		if (!CollectionUtils.isEmpty(targets)) {
			query.append("target", new BasicDBObject("$in", targets));
		}
		Iterable<DBObject> results = getAggregate(query);
		return transformLikeResult(results);
	}

	public LikeRecord getTargetLiked(String type, String target) {
		BasicDBObject query = new BasicDBObject("type", type).append("target", target);
		Iterable<DBObject> results = getAggregate(query);
		/**
		 * 组装
		 */
		LikeRecord record = new LikeRecord();
		record.setTarget(target);
		for (DBObject dbObject : results) {
			// { "_id" : { "target" : "hello1" , "like" : "false"} , "count" :  1}
			DBObject id = (DBObject) dbObject.get("_id");
			String targetstr = (String) id.get("target");
			boolean like = (boolean) id.get("like");
			int count = (int) dbObject.get("count");
			if (like) {
				record.setLikeNum(count);
			} else {
				record.setUnlikeNum(count);
			}
//			System.out.println(targetstr + "  " + like + "  " + count);
		}
		return record;
	}

	private Iterable<DBObject> getAggregate(DBObject query) {
		DBObject match = new BasicDBObject("$match", query);
		DBObject group = new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("target", "$target").append("like", "$like")).append("count", new BasicDBObject("$sum", 1)));
		List<DBObject> pipeline = new ArrayList<>();
		pipeline.add(match);
		pipeline.add(group);
		DBCollection collection = mongoTemplate.getCollection("likes");
		AggregationOutput aggregate = collection.aggregate(pipeline);
		Iterable<DBObject> results = aggregate.results();
		return results;
	}

	private List<LikeRecord> transformLikeResult(Iterable<DBObject> results) {
		Map<String, LikeRecord> map = new HashMap<>();
		for (DBObject dbObject : results) {
			// { "_id" : { "target" : "hello1" , "like" : "false"} , "count" :
			// 1}
			DBObject id = (DBObject) dbObject.get("_id");
			String target = (String) id.get("target");
			boolean like = (boolean) id.get("like");
			int count = (int) dbObject.get("count");

			LikeRecord record = map.get(target);
			if (record == null) {
				record = new LikeRecord();
			}
			record.setTarget(target);
			if (like) {
				record.setLikeNum(record.getLikeNum() + count);
			} else {
				record.setUnlikeNum(record.getUnlikeNum() + count);
			}
			map.put(target, record);
		}
		return MapUtil.getMapValue(map);
	}

	public class LikeRecord {

		private String target;
		private Integer likeNum = 0;
		private Integer unlikeNum = 0;

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public Integer getLikeNum() {
			return likeNum;
		}

		public void setLikeNum(Integer likeNum) {
			this.likeNum = likeNum;
		}

		public Integer getUnlikeNum() {
			return unlikeNum;
		}

		public void setUnlikeNum(Integer unlikeNum) {
			this.unlikeNum = unlikeNum;
		}

	}

}
