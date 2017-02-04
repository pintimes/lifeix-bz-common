package com.lifeix.bz.common.module.comment.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.bz.common.module.comment.CommentConfig;
import com.lifeix.bz.common.module.comment.model.Reply;
import com.lifeix.bz.common.module.comment.service.impl.ReplyService;
import com.lifeix.bz.common.module.comment.util.UserUtil;
import com.lifeix.football.common.exception.AuthorizationException;
import com.lifeix.football.common.model.User;
import com.lifeix.football.common.util.AuthorizationUtil;
import com.lifeix.football.common.util.StringUtil;

@RestController
@RequestMapping(value = "/comment/replies")
public class ReplyController {

	@Autowired
	private ReplyService replyService;

	@Autowired
	private CommentConfig appConfig;

	/**
	 * 
	 * @param groups
	 * @param post
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Reply addReply(// 插入评论
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@RequestParam(value = "commentId", required = true) String commentId, // 评论Id
			@RequestParam(value = "replyId", required = false) String replyId, // 回复Id
			@RequestParam(value = "images", required = false) String imagestr, // 图片数组
			@RequestParam(value = "content", required = false) String content) {// 文本内容
		/**
		 * 认证User
		 */
		AuthorizationUtil.userAuthorization(groups);
		/**
		 * 获得评论用户
		 */
		User user = getUserById(userId);
		if (user == null) {
			throw new AuthorizationException();
		}
		List<String> images = StringUtil.strToList(imagestr);

		return replyService.addReply(user, commentId, replyId, images, content);
	}

	/**
	 * 删除回复
	 * 
	 * @param groups
	 * @param postIds
	 */
	@RequestMapping(value = "/{replyId}", method = RequestMethod.DELETE)
	public void deleteReply(// 删除用户回复
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@PathVariable(value = "replyId") String replyId) {// 回复
		if (AuthorizationUtil.checkAdminAuth()) {
			replyService.delete(replyId);
			return ;
		}
		User user = getUserById(userId);
		if (user == null) {
			throw new AuthorizationException();
		}
		replyService.deleteReplyByUser(userId, replyId);
	}

	/**
	 * 查询评论的回复 默认按时间倒叙
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午1:43:23
	 *
	 * @param groups
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Reply> listReplies(// 查询评论
			@RequestParam(value = "commentId", required = true) String commentId, //
			@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit, // 一页长度
			@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") Date date) {//
		return replyService.listReplies(commentId, limit, date);
	}

	/**
	 * 获得用户
	 * 
	 * @version 2016年11月8日上午9:48:53
	 *
	 * @param userId
	 * @return
	 */
	private User getUserById(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return null;
		}
		String ip = appConfig.getUserhost();
		String port = appConfig.getUserport();
		return UserUtil.getUser(ip, port, userId);
	}
}
