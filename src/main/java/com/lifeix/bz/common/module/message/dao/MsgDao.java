package com.lifeix.bz.common.module.message.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lifeix.bz.common.module.message.model.MsgCountInfo;
import com.lifeix.bz.common.module.message.po.MsgPO;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class MsgDao {

	@Autowired
	private MongoTemplate template;

	public void insert(MsgPO po) {
		template.insert(po);
	}

	public void delete(String userId, List<String> msgIds) {
		Query query = new Query();
		query.addCriteria(Criteria.where("receiverId").is(userId));
		query.addCriteria(Criteria.where("id").in(msgIds));
		template.remove(query, MsgPO.class);
	}

	public void delete(List<String> msgIds) {
		Query query = new Query();
		Criteria criteria = Criteria.where("id").in(msgIds);
		query.addCriteria(criteria);
		template.remove(query, MsgPO.class);
	}
	
	public void updateUserMsgReadStatus(String userId, String[] ids, boolean read) {
		Query query = new Query();
		query.addCriteria(Criteria.where("receiverId").is(userId));
		query.addCriteria(Criteria.where("id").in(Arrays.asList(ids)));
		Update update = new Update();
		update.set("read", read);
		template.updateFirst(query, update, MsgPO.class);
	}
	
	public void updateMsgReadStatusByCategory(String userId,String app, String categoryId,  boolean read) {
		Query query = new Query();
		query.addCriteria(Criteria.where("receiverId").is(userId));
		query.addCriteria(Criteria.where("app").is(app));
		query.addCriteria(Criteria.where("categoryId").is(categoryId));
		Update update = new Update();
		update.set("read", read);
		template.updateMulti(query, update, MsgPO.class);
	}

	public MsgPO findAndModifyRead(String msgId, boolean read) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(msgId));
		Update update = new Update();
		update.set("read", read);
		return template.findAndModify(query, update, MsgPO.class);
	}

	public MsgPO find(String id) {
		return template.findById(id, MsgPO.class);
	}

	public List<MsgPO> list(String app, String categoryId, String type, String userId, int num, Date createTime) {
		Query query = new Query();
		query.limit(num);
		query.addCriteria(Criteria.where("app").is(app));
		if (!StringUtils.isEmpty(categoryId)) {
			query.addCriteria(Criteria.where("categoryId").is(categoryId));
		}
		if (!StringUtils.isEmpty(type)) {
			query.addCriteria(Criteria.where("type").is(type));
		}
		if (!StringUtils.isEmpty(userId)) {
			query.addCriteria(Criteria.where("receiverId").is(userId));
		}
		query.addCriteria(Criteria.where("createTime").lt(createTime));
		/**
		 * 排序
		 */
		Sort.Direction s = Sort.Direction.DESC;
		Sort sort = new Sort(s, "createTime");
		query.with(sort);
		// Field fields = query.fields();
		return template.find(query, MsgPO.class);
	}

	public long countUnread(String app, String userId,String categoryId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("app").is(app));
		query.addCriteria(Criteria.where("receiverId").is(userId));
		query.addCriteria(Criteria.where("read").is(false));
		if (!StringUtils.isEmpty(categoryId)) {
			query.addCriteria(Criteria.where("categoryId").is(categoryId));	
		}
		return template.count(query, MsgPO.class);
	}

	public MsgCountInfo count(String app, String userId) {
		BasicDBObject query = new BasicDBObject("app", app).append("receiverId", userId);
		DBObject match = new BasicDBObject("$match", query);
		DBObject group = new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("receiverId", "$receiverId").append("read", "$read")).append("count", new BasicDBObject("$sum", 1)));
		List<DBObject> pipeline = new ArrayList<>();
		pipeline.add(match);
		pipeline.add(group);
		
		String collectionName = template.getCollectionName(MsgPO.class);
		DBCollection collection = template.getCollection(collectionName);
		AggregationOutput aggregate = collection.aggregate(pipeline);
		Iterable<DBObject> results = aggregate.results();
		MsgCountInfo msgInfo = new MsgCountInfo();
		for (DBObject dbObject : results) {
			DBObject id = (DBObject) dbObject.get("_id");
//			String receiverId = (String) id.get("receiverId");
			boolean read = (boolean) id.get("read");
			int count = (int) dbObject.get("count");
			if (read) {
				msgInfo.setRead(count);
			} else {
				msgInfo.setUnread(count);
			}
		}
		msgInfo.setNum(msgInfo.getRead()+msgInfo.getUnread());
		return msgInfo;
	}

	private Iterable<DBObject> getAggregate(DBObject query) {
		DBObject match = new BasicDBObject("$match", query);
		DBObject group = new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("target", "$target").append("like", "$like")).append("count", new BasicDBObject("$sum", 1)));
		List<DBObject> pipeline = new ArrayList<>();
		pipeline.add(match);
		pipeline.add(group);
		String collectionName = template.getCollectionName(MsgPO.class);
		DBCollection collection = template.getCollection(collectionName);

		AggregationOutput aggregate = collection.aggregate(pipeline);
		Iterable<DBObject> results = aggregate.results();
		return results;
	}

	public void clear() {
		template.remove(new Query(), MsgPO.class);
	}

}
