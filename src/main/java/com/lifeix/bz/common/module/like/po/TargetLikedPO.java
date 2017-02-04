package com.lifeix.bz.common.module.like.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用来存储Like Record
 * 
 * @author gcc
 */
@Document(collection = "like-targets")
public class TargetLikedPO {
	
	@Id
	private String id;// type#target

	private int likeNum;

	private int unlikeNum;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}

	public int getUnlikeNum() {
		return unlikeNum;
	}

	public void setUnlikeNum(int unlikeNum) {
		this.unlikeNum = unlikeNum;
	}

}
