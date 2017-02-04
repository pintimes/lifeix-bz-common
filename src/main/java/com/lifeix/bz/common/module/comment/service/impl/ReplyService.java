package com.lifeix.bz.common.module.comment.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.lifeix.bz.common.module.comment.dao.CommentDao;
import com.lifeix.bz.common.module.comment.dao.ReplyDao;
import com.lifeix.bz.common.module.comment.model.Reply;
import com.lifeix.bz.common.module.comment.po.CommentPO;
import com.lifeix.bz.common.module.comment.po.ReplyPO;
import com.lifeix.bz.common.module.comment.util.ImageCheck;
import com.lifeix.bz.common.module.comment.util.TextCheck;
import com.lifeix.football.common.exception.AuthorizationException;
import com.lifeix.football.common.exception.BusinessException;
import com.lifeix.football.common.model.User;
import com.lifeix.football.common.util.AdapterUtil;

@Service
public class ReplyService {

	private Logger logger = LoggerFactory.getLogger(ReplyService.class);

	@Autowired
	private ReplyDao replyDao;

	@Autowired
	private CommentDao commentDao;

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
	public Reply addReply(User user, String commentId, String replyId, List<String> images, String content) {
		/**
		 * 校验
		 */
		if (user == null) {
			throw new AuthorizationException("用户不存在");
		}
		if (CollectionUtils.isEmpty(images) && StringUtils.isEmpty(content)) {
			throw new BusinessException("内容不能为空");
		}
		ImageCheck.checkImages(images);
		TextCheck.checkText(content);
		/**
		 * 校验评论
		 */
		CommentPO commentPO = commentDao.find(commentId);
		if (commentPO == null) {
			throw new BusinessException("评论已删除");
		}
		ReplyPO replyPO = null;
		if (!StringUtils.isEmpty(replyId)) {
			replyPO = replyDao.find(replyId);
			if (replyPO == null) {
				throw new BusinessException("回复已删除");
			}
			if (!replyPO.getCommentId().equals(commentPO.getId())) {
				throw new BusinessException("回复失败");
			}
		}

		/**
		 * 创建数据对象
		 */
		ReplyPO po = new ReplyPO();
		// 评论人信息
		po.setUser(user);
		po.setFromUserId(user.getId());
		// 内容
		po.setImages(images);
		po.setContent(content);
		// 评论目标信息
		po.setSubjectType(commentPO.getSubjectType());
		po.setSubjectId(commentPO.getSubjectId());
		// 回复目标
		if (replyPO != null) {
			po.setTargetUser(replyPO.getUser());
		}
		po.setCommentId(commentId);
		po.setReplyId(replyId);
		// 评论时间
		po.setCreateTime(new Date());
		replyDao.insert(po);
		/**
		 * 评论数目+1
		 */
		commentDao.increaseReplysum(commentId);
		
		/**
		 * 返回评论对象给前端
		 */
		Reply reply = AdapterUtil.toT(po, Reply.class);
		return reply;
	}

	/**
	 * 删除评论，只适合于管理员删除
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午6:45:45
	 *
	 * @param commentId
	 */
	public void delete(String replyId) {
		logger.debug("replyId= {}", replyId);
		ReplyPO p = replyDao.findAndRemove(replyId);
		if (p!=null) {
			commentDao.decreaseReplysum(p.getCommentId());
		}
	}

	/**
	 * 删除评论，只适合于用户删除
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午6:45:58
	 *
	 * @param userId
	 * @param commentId
	 */
	public void deleteReplyByUser(String userId,String replyId) {
		logger.debug("userId={} , replyId= {}", userId, replyId);
		ReplyPO p = replyDao.findAndRemove(userId, replyId);
		if (p!=null) {
			commentDao.decreaseReplysum(p.getCommentId());
		}
	}

	/**
	 * 查询评论下回复列表
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年12月15日下午5:10:28
	 *
	 * @param commentId
	 * @param limit
	 * @param date
	 * @return
	 */
	public List<Reply> listReplies(String commentId, int limit, Date date) {
		Date createTime = null;
		if (date == null) {
			createTime = new Date(0);
		} else {
			createTime = date;
		}
		int num = Math.max(1, limit);
		num = Math.min(100, limit);
		List<ReplyPO> replyPOs = replyDao.findRepliesByComment(commentId, num, createTime);
		if (CollectionUtils.isEmpty(replyPOs)) {
			return null;
		}
		return AdapterUtil.toTs(replyPOs, Reply.class);
	}

}
