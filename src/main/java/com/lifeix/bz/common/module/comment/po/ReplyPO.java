package com.lifeix.bz.common.module.comment.po;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.lifeix.football.common.model.User;

@Document(collection = "replies")
public class ReplyPO {

	@Id
	private String id = null;

	@Indexed
	private String fromUserId;

	@Field
	private User user = null;

	/**
	 * 主题类型
	 */
	private String subjectType;

	/**
	 * 主题ID
	 */
	private String subjectId;

	/**
	 * 是哪条评论下的回复
	 */
	@Indexed
	private String commentId;

	/**
	 * 2次回复
	 */
	@Indexed
	private String replyId;

	@Field
	private User targetUser;

	private List<String> images;

	private String content = null;

	@Indexed
	private Date createTime = null;
	
	/**
	 * 喜欢的数目
	 */
	private long likeNum;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
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

	public long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(long likeNum) {
		this.likeNum = likeNum;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public User getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(User targetUser) {
		this.targetUser = targetUser;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getReplyId() {
		return replyId;
	}

	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}

}
