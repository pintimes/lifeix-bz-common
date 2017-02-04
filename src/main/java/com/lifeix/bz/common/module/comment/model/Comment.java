
package com.lifeix.bz.common.module.comment.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment extends CommonComment{
	
	private List<Reply> replies;
	
	private long replySum;
	
	public List<Reply> getReplies() {
		return replies;
	}

	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}

	public long getReplySum() {
		return replySum;
	}

	public void setReplySum(long replySum) {
		this.replySum = replySum;
	}

	
}
