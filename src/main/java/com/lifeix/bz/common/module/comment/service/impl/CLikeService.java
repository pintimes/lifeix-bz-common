package com.lifeix.bz.common.module.comment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.lifeix.bz.common.module.comment.dao.CommentDao;
import com.lifeix.bz.common.module.comment.dao.CLikeDao;

@Service
public class CLikeService {

	@Autowired
	private CLikeDao likeDao;

	@Autowired
	private CommentDao commentDao;

	public void addLike(String userId, String commentId) {
		if (StringUtils.isEmpty(userId)) {
			return;
		}
		if (StringUtils.isEmpty(commentId)) {
			return;
		}
		boolean isUpdateOfExisting = likeDao.save(userId, commentId);
		if (isUpdateOfExisting) {
			return;
		}
		/**
		 * 评论数的like+1
		 */
		commentDao.increaseLikenum(commentId);
	}

	public void deleteLike(String userId, String commentId) {
		if (StringUtils.isEmpty(commentId)) {
			return;
		}
		if (StringUtils.isEmpty(userId)) {
			return;
		}
		/**
		 * 删除影响行数
		 */
		int num = likeDao.delete(userId, commentId);
		if (num != 1) {
			return;
		}
		/**
		 * 评论数的like-1
		 */
		commentDao.decreaseLikenum(commentId);
	}

	public List<String> find(String userId, List<String> commentIds) {
		return likeDao.findCommentIds(userId, commentIds);
	}

}
