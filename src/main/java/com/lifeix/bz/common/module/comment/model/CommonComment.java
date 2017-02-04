
package com.lifeix.bz.common.module.comment.model;

import java.util.Date;
import java.util.List;

import com.lifeix.football.common.model.User;

public class CommonComment {

	private String id = null;

	/**
	 * 评论人
	 */
	private User user = null;

	/**
	 * 评论图片
	 */
	private List<String> images;

	/**
	 * 评论内容
	 */
	private String content = null;

	/**
	 * 主题类型
	 */
	private String subjectType;

	/**
	 * 主题ID
	 */
	private String subjectId;

	/**
	 * 创建时间
	 */
	private Date createTime = null;

	/**
	 * 喜欢的数目
	 */
	private long likeNum;

	/**
	 * 是否点过赞
	 */
	private boolean like;

	
	
	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(long likeNum) {
		this.likeNum = likeNum;
	}

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}

}
