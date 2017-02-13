package com.lifeix.bz.common.module.comment.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.lifeix.bz.common.module.comment.dao.CommentDao;
import com.lifeix.bz.common.module.comment.dao.ReplyDao;
import com.lifeix.bz.common.module.comment.model.Comment;
import com.lifeix.bz.common.module.comment.model.Count;
import com.lifeix.bz.common.module.comment.model.Reply;
import com.lifeix.bz.common.module.comment.model.SubjectComment;
import com.lifeix.bz.common.module.comment.po.CommentPO;
import com.lifeix.bz.common.module.comment.po.ReplyPO;
import com.lifeix.bz.common.module.comment.util.ImageCheck;
import com.lifeix.bz.common.module.comment.util.TextCheck;
import com.lifeix.football.common.exception.AuthorizationException;
import com.lifeix.football.common.exception.BusinessException;
import com.lifeix.football.common.model.User;
import com.lifeix.football.common.util.AdapterUtil;

@Service
public class CommentService {

	private Logger logger = LoggerFactory.getLogger(CommentService.class);

	@Autowired
	private CommentDao commentDao;
	
	@Autowired
	private ReplyDao replyDao;

	@Autowired
	private CLikeService likeService;

	/**
	 * 添加评论
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午5:50:18
	 *
	 * @param user
	 * @param targetType
	 * @param targetId
	 * @param images
	 * @param content
	 * @return
	 */
	public Comment addComment(User user, String subjectType, String subjectId, List<String> images, String content) {
		/**
		 * 校验
		 */
		if (user == null) {
			throw new AuthorizationException("用户不存在");
		}
		if (StringUtils.isEmpty(subjectType)) {
			throw new BusinessException("subjectType.empty");
		}
		if (StringUtils.isEmpty(subjectType)) {
			throw new BusinessException("targetType.empty");
		}
		if (StringUtils.isEmpty(images) && StringUtils.isEmpty(content)) {
			throw new BusinessException("请输入评论内容");
		}
		// 校验图片
		ImageCheck.checkImages(images);
		// 校验文本
		TextCheck.checkText(content);
		/**
		 * 创建数据对象
		 */
		CommentPO po = new CommentPO();
		// 评论人信息
		po.setUser(user);
		po.setFromUserId(user.getId());
		// 内容
		po.setImages(images);
		po.setContent(content);
		// 评论目标信息
		po.setSubjectType(subjectType);
		po.setSubjectId(subjectId);
		// 评论时间
		po.setCreateTime(new Date());
		commentDao.insert(po);
		/**
		 * 返回评论对象给前端
		 */
		return AdapterUtil.toT(po, Comment.class);
	}


	/**
	 * 获得一个Subject的评论列表，回复取决于前端调用
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月14日上午10:51:05
	 *
	 * @param subjectType
	 * @param subjectId
	 * @param limit
	 * @param date
	 * @param containReply
	 * @param replyLimit
	 * @return
	 */
	public List<Comment> getSubjectComments(String userId, String subjectType, String subjectId, int limit, Date date, boolean containReply, int replyLimit) {
		Date createTime = null;
		if (date == null) {
			createTime = new Date();
		} else {
			createTime = date;
		}
		int num = Math.max(1, limit);
		num = Math.min(100, limit);
		/**
		 * 获取到所有的评论
		 */
		List<CommentPO> commentpos = commentDao.find(subjectType, subjectId, num, createTime);
		if (CollectionUtils.isEmpty(commentpos)) {
			return null;
		}
		List<Comment> comments = AdapterUtil.toTs(commentpos, Comment.class);
		List<String> commentIds = comments.stream().map((Comment p) -> p.getId()).collect(Collectors.toList());
		// 不包含回复则直接返回
		if (!containReply) {
			return comments;
		}
		/**
		 * 将评论对应的最新回复插入到评论中
		 */
		Map<String, List<ReplyPO>> replypos = replyDao.findReplies(commentIds, replyLimit,new Date(0));
		if (CollectionUtils.isEmpty(replypos)) {
			return comments;
		}
		comments.stream().forEach(p -> p.setReplies(AdapterUtil.toTs(replypos.get(p.getId()), Reply.class)));

		return combinLikes(userId, comments);
	}

	/**
	 * 获得多个Subject的评论列表，不需要回复
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月14日上午10:50:28
	 *
	 * @param subjectType
	 * @param subjectIds
	 * @param commentLimit
	 * @return
	 */
	public Map<String, SubjectComment> getCommentMapByMutiSubjects(String subjectType, List<String> subjectIds, int commentLimit) {
		if (CollectionUtils.isEmpty(subjectIds)) {
			return null;
		}
		Map<String, List<CommentPO>> commentpos = commentDao.findSubjectComments(subjectType, subjectIds, commentLimit);
		if (CollectionUtils.isEmpty(commentpos)) {
			return null;
		}
		/**
		 * 每一个Subject的评论数目
		 */
		Map<String, Long> subjectCommentNumMap = commentDao.count(subjectType, subjectIds);
		/**
		 * 转换成Subject Map
		 */
		Map<String, SubjectComment> map = new HashMap<>();
		commentpos.forEach((String k, List<CommentPO> l) -> map.put(k, toSubjectComment(k, l, subjectCommentNumMap)));
		return map;
	}

	private SubjectComment toSubjectComment(String subjectId, List<CommentPO> pos, Map<String, Long> subjectCommentNumMap) {
		SubjectComment subjectComment = new SubjectComment(subjectId, AdapterUtil.toTs(pos, Comment.class));
		if (!CollectionUtils.isEmpty(subjectCommentNumMap)) {
			Long l = subjectCommentNumMap.get(subjectId);
			subjectComment.setCommentNum(l==null?0:l.longValue());
		}
		return subjectComment;
	}

	private List<Comment> combinLikes(String userId, List<Comment> comments) {
		if (CollectionUtils.isEmpty(comments)) {
			return comments;
		}
		if (StringUtils.isEmpty(userId)) {
			return comments;
		}
		List<String> commentIds = new ArrayList<>();
		comments.stream().forEach((Comment c) -> commentIds.addAll(getCommentIds(c)));
		List<String> set = likeService.find(userId, commentIds);
		if (CollectionUtils.isEmpty(set)) {
			return comments;
		}
		comments.stream().forEach((Comment c) -> addLike(c, set));
		return comments;
	}

	private void addLike(Comment c, final List<String> set) {
		c.setLike(set.contains(c.getId()));
		if (CollectionUtils.isEmpty(c.getReplies())) {
			return;
		}
		for (Reply r : c.getReplies()) {
			r.setLike(set.contains(r.getId()));
		}
	}

	private List<String> getCommentIds(Comment c) {
		List<String> commentIds = new ArrayList<>();
		commentIds.add(c.getId());
		if (CollectionUtils.isEmpty(c.getReplies())) {
			return commentIds;
		}
		for (Reply r : c.getReplies()) {
			commentIds.add(r.getId());
		}
		return commentIds;
	}

	/**
	 * 删除评论，适合于管理员删除
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午6:45:45
	 *
	 * @param commentId
	 */
	public void delete(String commentId) {
		logger.debug("commentId= {}", commentId);
		commentDao.remove(commentId);
	}

	/**
	 * 删除评论，适合于用户删除
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午6:45:58
	 *
	 * @param userId
	 * @param commentId
	 */
	public void delete(String userId, String commentId) {
		logger.debug("userId={} , commentId= {}", userId, commentId);
		commentDao.delete(userId, commentId);
	}

	public List<Count> countComment(String subjectType, List<String> subjectIds) {
		if (CollectionUtils.isEmpty(subjectIds)) {
			return  null;
		}
		Map<String, Long> map = commentDao.count(subjectType, subjectIds);
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}

		List<Count> list = new ArrayList<>();
		map.forEach((String k, Long v) -> list.add(new Count(k, v)));
		return list;
	}
	
	private List<Count> getCounts(List<String> subjectIds){
		List<Count> counts = new ArrayList<>();
		for (String subjectId : subjectIds) {
			Count count = new Count();
			count.setKey(subjectId);
			count.setNum(0);
			counts.add(count);
		}
		return counts;
	}

}
