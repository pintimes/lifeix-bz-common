package com.lifeix.bz.common.module.message.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.lifeix.bz.common.module.message.dao.MsgDao;
import com.lifeix.bz.common.module.message.model.MsgCountInfo;
import com.lifeix.bz.common.module.message.po.MsgPO;
import com.lifeix.football.common.exception.BusinessException;
import com.lifeix.football.common.exception.IllegalparamException;
import com.lifeix.football.common.model.Message;
import com.lifeix.football.common.model.User;
import com.lifeix.football.common.util.AdapterUtil;
import com.lifeix.football.common.util.StringUtil;

@Service
public class MsgService {

	private Logger logger = LoggerFactory.getLogger(MsgService.class);

	@Autowired
	private MsgDao msgDao;

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
	public Message insert(Message msg) {
		// 校验
		validateMessage(msg);
		// 保存到数据库
		msg.setCreateTime(new Date());
		MsgPO po = AdapterUtil.toT(msg, MsgPO.class);
		po.setSenderId(msg.getSender().getId());
		po.setReceiverId(msg.getReceiver().getId());
		msgDao.insert(po);
		msg.setId(po.getId());
		return msg;
	}

	/**
	 * 校验
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午5:45:23
	 *
	 * @param user
	 * @param targetType
	 * @param targetId
	 * @param images
	 * @param content
	 */
	private void validateMessage(Message msg) {
		if (msg == null) {
			throw new BusinessException("msg.empty");
		}
		User sender = msg.getSender();
		if (sender == null) {
			throw new BusinessException("msg.sender.empty");
		}
		if (StringUtils.isEmpty(sender.getId())) {
			throw new BusinessException("msg.sender.id.empty");
		}
		User receiver = msg.getReceiver();
		if (receiver == null) {
			throw new BusinessException("msg.receiver.empty");
		}
		if (StringUtils.isEmpty(receiver.getId())) {
			throw new BusinessException("msg.receiver.id.empty");
		}
		if (StringUtils.isEmpty(msg.getApp())) {
			throw new BusinessException("msg.app.empty");
		}
		if (StringUtils.isEmpty(msg.getCategoryId())) {
			throw new BusinessException("msg.category.empty");
		}
		if (StringUtils.isEmpty(msg.getType())) {
			throw new BusinessException("msg.type.empty");
		}
		if (StringUtils.isEmpty(msg.getTitle())) {
			throw new BusinessException("msg.title.empty");
		}
	}

	/**
	 * 查询消息
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午2:55:01
	 *
	 * @param targetType
	 * @param targetId
	 * @param limit
	 * @param date
	 * @return
	 */
	public List<Message> list(String app, String categoryId, String type, String userId, int limit, Date date) {
		if (StringUtils.isEmpty(app)) {
			throw new BusinessException("app.empty");
		}
		int num = Math.max(limit, 1);
		num = Math.min(num, 100);
		Date createTime = date;
		if (createTime == null) {
			createTime = new Date();
		}
		// 查询
		List<MsgPO> pos = msgDao.list(app, categoryId, type, userId, num, createTime);
		return toDtos(pos);
	}
	
	
	public long countUserUnread(String app, String userId,String categoryId) {
		if (StringUtils.isEmpty(app)) {
			throw new BusinessException("app.empty");
		}
		if (StringUtils.isEmpty(userId)) {
			throw new BusinessException("userId.empty");
		}
		// 查询
		return msgDao.countUnread(app, userId,categoryId);
	}
	
	/**
	 * 查询消息
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午2:55:01
	 *
	 * @param targetType
	 * @param targetId
	 * @param limit
	 * @param date
	 * @return
	 */
	public MsgCountInfo count(String app, String userId) {
		if (StringUtils.isEmpty(app)) {
			throw new BusinessException("app.empty");
		}
		if (StringUtils.isEmpty(userId)) {
			throw new BusinessException("userId.empty");
		}
		// 查询
		return msgDao.count(app,  userId);
	}
	
	/**
	 * 删除:适合于管理员删除
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午6:45:45
	 *
	 * @param commentId
	 */
	public void delete(String messageIds) {
		List<String> list = StringUtil.strToList(messageIds);
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		msgDao.delete(list);
	}

	/**
	 * 删除，适合于用户删除
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午6:45:58
	 *
	 * @param userId
	 * @param commentId
	 */
	public void delete(String userId, String msgIds) {
		if (StringUtils.isEmpty(userId)) {
			return ;
		}
		List<String> list = StringUtil.strToList(msgIds);
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		msgDao.delete(userId, list);
	}

	/**
	 * 管理员读取消息
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月8日下午4:50:03
	 *
	 * @param userId
	 * @param msgId
	 * @return
	 */
	public Message read(String msgId) {
		return toDto(msgDao.find(msgId));
	}
	
	/**
	 * 管理员读取消息
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月8日下午4:50:03
	 *
	 * @param userId
	 * @param msgId
	 * @return
	 */
	public void notifyRead(String userId, String idstr) {
		if (StringUtils.isEmpty(idstr)) {
			return ;
		}
		String[] ids = idstr.split(",");
		msgDao.updateUserMsgReadStatus(userId,ids,true);
	}
	
	public void notifyReadCategory(String userId,String app, String categoryId) {
		if (StringUtils.isEmpty(app) || StringUtils.isEmpty(categoryId)) {
			throw new IllegalparamException("请核对参数");
		}
		msgDao.updateMsgReadStatusByCategory(userId,app,categoryId,true);
	}


	/**
	 * 用户读取消息，打开消息后read=true
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月8日下午4:50:03
	 *
	 * @param userId
	 * @param msgId
	 * @return
	 */
	public Message read(String userId, String msgId) {
		boolean read = true;
		MsgPO msgPO = msgDao.findAndModifyRead(msgId, read);
		if (msgPO == null) {
			return null;
		}
		msgPO.setRead(read);
		return toDto(msgPO);
	}

	
	private List<Message> toDtos(List<MsgPO> pos) {
		if (CollectionUtils.isEmpty(pos)) {
			return null;
		}
		List<Message> result = new ArrayList<>();
		for (int i = 0, len = pos.size(); i < len; i++) {
			result.add(toDto(pos.get(i)));
		}
		return result;
	}

	private Message toDto(MsgPO po) {
		return AdapterUtil.toT(po, Message.class);
	}

}
