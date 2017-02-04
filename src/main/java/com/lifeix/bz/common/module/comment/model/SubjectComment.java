package com.lifeix.bz.common.module.comment.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectComment {

	private String subjectId;

	private List<Comment> comments;

	private long commentNum;

	public SubjectComment() {
		super();
	}

	public SubjectComment(String subjectId, List<Comment> comments) {
		super();
		this.subjectId = subjectId;
		this.comments = comments;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(long commentNum) {
		this.commentNum = commentNum;
	}

}
