
package com.lifeix.bz.common.module.comment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lifeix.football.common.model.User;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reply extends CommonComment{
	
	private String commentId;

	private User targetUser;
	
	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public User getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(User targetUser) {
		this.targetUser = targetUser;
	}
	
}
