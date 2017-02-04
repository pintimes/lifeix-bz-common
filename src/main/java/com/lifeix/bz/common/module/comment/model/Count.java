package com.lifeix.bz.common.module.comment.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Count {

	private String key;
	private long num;
	
	public Count() {
		super();
	}

	public Count(String key, long num) {
		super();
		this.key = key;
		this.num = num;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

}
