package com.lifeix.bz.common.module.comment.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.lifeix.bz.common.module.comment.model.Comment;
import com.lifeix.bz.common.module.comment.model.Count;
import com.lifeix.bz.common.module.comment.model.SubjectComment;
import com.lifeix.bz.common.module.comment.service.impl.CommentService;
import com.lifeix.bz.common.module.comment.util.UserUtil;
import com.lifeix.football.common.exception.AuthorizationException;
import com.lifeix.football.common.model.User;
import com.lifeix.football.common.util.AuthorizationUtil;
import com.lifeix.football.common.util.StringUtil;

@RestController
@RequestMapping(value = "/comment/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentConfig appConfig;

	/**
	 * 用户评论，可以对任意类型的target进行评论
	 * 
	 * @param groups
	 * @param post
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Comment addComment(// 插入评论
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@RequestParam(value = "subjectType", required = true) String subjectType, // 目标类型
			@RequestParam(value = "subjectId", required = true) String subjectId, // 目标ID
			@RequestParam(value = "images", required = false) String images, // 图片数组
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
		return commentService.addComment(user, subjectType, subjectId, StringUtil.strToList(images), content);
	}

	/**
	 * 删除文章
	 * 
	 * @param groups
	 * @param postIds
	 */
	@RequestMapping(value = "/{commentId}", method = RequestMethod.DELETE)
	public void deleteComment(// 删除用户评论
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@PathVariable(value = "commentId") String commentId) {// 评论Id
		if (AuthorizationUtil.checkAdminAuth()) {// 管理员权限
			commentService.delete(commentId);
			return ;
		}
		AuthorizationUtil.userAuthorization(groups);
		commentService.delete(userId, commentId);
	}

	/**
	 * 根据目标类型以及目标Id获取评论列表，适合于查看详细
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月14日下午6:10:22
	 *
	 * @param groups
	 * @param userId
	 * @param subjectType
	 * @param subjectId
	 * @param limit
	 * @param date
	 * @param containReply
	 * @param replyLimit
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Comment> getSubjectComments(// 查询评论
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@RequestParam(value = "subjectType", required = true) String subjectType, // 目标类型
			@RequestParam(value = "subjectId", required = true) String subjectId, // 目标ID
			@RequestParam(value = "limit", required = false, defaultValue = "10") int limit, // 一页长度
			@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") Date date, // 时间
			@RequestParam(value = "containReply", required = false, defaultValue = "false") boolean containReply, // 是否包含回复
			@RequestParam(value = "replyLimit", required = false, defaultValue = "4") int replyLimit) {// 回复的数目
		List<Comment> temps = commentService.getSubjectComments(userId, subjectType, subjectId, limit, date, containReply, replyLimit);
		return temps;
	}

	/**
	 * 根据目标类型以及目标Id获取评论列表，适合于查看详细
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月14日下午6:10:22
	 *
	 * @param groups
	 * @param userId
	 * @param subjectType
	 * @param subjectId
	 * @param limit
	 * @param date
	 * @param containReply
	 * @param replyLimit
	 * @return
	 */
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public List<Count> countComment(// 查询评论
			@RequestParam(value = "subjectType", required = true) String subjectType, // 目标类型
			@RequestParam(value = "subjectIds", required = true) String subjectIds) {// id
		return commentService.countComment(subjectType, StringUtil.strToList(subjectIds));
	}

	/**
	 * 返回一种类型的评论列表 比如你来判的列表中每一个选项都需要三个评论
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月14日下午6:10:10
	 *
	 * @param groups
	 * @param userId
	 * @param subjectType
	 * @param subjectIds
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/map", method = RequestMethod.GET)
	public Map<String, SubjectComment> getCommentMapByMutiSubjects(// 查询评论
			@RequestParam(value = "subjectType", required = true) String subjectType, // 目标类型
			@RequestParam(value = "subjectIds", required = true) String subjectIds, // 目标ID列表
			@RequestParam(value = "limit", required = false, defaultValue = "3") Integer limit) {// 评论数目
		return commentService.getCommentMapByMutiSubjects(subjectType, StringUtil.strToList(subjectIds), limit);
	}

	/**
	 * 获得用户
	 * 
	 * @version 2016年11月8日上午9:48:53
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
