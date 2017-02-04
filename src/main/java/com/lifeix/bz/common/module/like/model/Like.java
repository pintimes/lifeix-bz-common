package com.lifeix.bz.common.module.like.model;

import java.io.Serializable;

/**
 * 喜欢
 * 
 * @author gcc
 */ 
public class Like {

	private String type;// 目标类型 player,post

	private String target;

	private int likeNum;

	private int unlikeNum;
	
	private Boolean like ; //null ,true like ,false unlike 

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Boolean getLike() {
		return like;
	}

	public void setLike(Boolean like) {
		this.like = like;
	}

}
