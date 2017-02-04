package com.lifeix.bz.common.module.comment.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public abstract class CommonDao<T> {

	@Autowired
	protected MongoTemplate template;

	public void insert(T t) {
		template.save(t);
	}

	public void save(T t) {
		template.save(t);
	}
	
	public T remove(String id) {
		Query query = new Query();
		Criteria criteria = Criteria.where("id").is(id);
		query.addCriteria(criteria);
		return template.findAndRemove(query, getClassname());
	}

	public T findAndRemove(String id) {
		Query query = new Query();
		Criteria criteria = Criteria.where("id").is(id);
		query.addCriteria(criteria);
		return template.findAndRemove(query, getClassname());
	}

	public T find(String id) {
		return template.findById(id, getClassname());
	}

	public abstract Class<T> getClassname();

}
