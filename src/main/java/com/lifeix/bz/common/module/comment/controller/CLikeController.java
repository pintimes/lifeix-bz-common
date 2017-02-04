package com.lifeix.bz.common.module.comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.bz.common.module.comment.service.impl.CLikeService;
import com.lifeix.football.common.util.AuthorizationUtil;

@RestController
@RequestMapping(value = "/comment/likes")
public class CLikeController {

	@Autowired
	private CLikeService likeService;

	/**
	 * 
	 * @param groups
	 * @param post
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void addLike(// 插入评论
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@RequestParam(value = "commentId") String commentId) {// 评论Id
		/**
		 * 认证User
		 */
		AuthorizationUtil.userAuthorization(groups);
		/**
		 * 获得评论用户
		 */
		likeService.addLike(userId, commentId);
	}

	/**
	 * 
	 * @param groups
	 * @param post
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public void deleteLike(// 插入评论
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@RequestParam(value = "commentId") String commentId) {// 评论Id
		/**
		 * 认证User
		 */
		AuthorizationUtil.userAuthorization(groups);
		/**
		 * 获得评论用户
		 */
		likeService.deleteLike(userId, commentId);
	}

}
