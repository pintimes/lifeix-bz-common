package com.lifeix.bz.common.module.message.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.bz.common.module.message.model.MsgCountInfo;
import com.lifeix.bz.common.module.message.service.impl.MsgService;
import com.lifeix.football.common.model.Message;
import com.lifeix.football.common.util.AuthorizationUtil;

@RestController
@RequestMapping(value = "/messages")
public class MsgController {

	@Autowired
	private MsgService msgService;

	/**
	 * 
	 * @param groups
	 * @param post
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Message insert(@RequestBody(required = true) Message msg) {// 文本内容
		/**
		 * 认证Admin
		 */
		AuthorizationUtil.adminAuth();
		/**
		 * 插入消息
		 */
		return msgService.insert(msg);
	}

	/**
	 * 用户查询自己的消息列表
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
	public List<Message> list(//
			@RequestHeader(value = "X-Consumer-Groups" , required = false) String groups, // user
			@RequestHeader(value = "X-Consumer-Custom-ID", required = false) String kongUserId, // 用户的唯一ID
			@RequestParam(value = "app", required = true, defaultValue = "c-f") String app, // app标识
			@RequestParam(value = "categoryId", required = false) String categoryId, // 类目标示
			@RequestParam(value = "type", required = false) String type, // 消息类型
			@RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit, // 一页长度
			@RequestParam(value = "date", required = false) Date date) {// 消息时间
		// 用户鉴权
		AuthorizationUtil.userAuth(groups);
		// 查询列表
		return msgService.list(app, categoryId, type, kongUserId, limit, date);
	}

	/**
	 * 用户查询自己未读消息数目
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午1:43:23
	 *
	 * @param groups
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/count/unread", method = RequestMethod.GET)
	public MsgCountInfo countUnreadnum(//
			@RequestHeader(value = "X-Consumer-Groups" , required = false) String groups, // user
			@RequestHeader(value = "X-Consumer-Custom-ID", required = false) String kongUserId, // 用户的唯一ID
			@RequestParam(value = "categoryId", required = false) String categoryId, // 类目标示
			@RequestParam(value = "app", required = true, defaultValue = "c-f") String app) {// app标识
		// 用户鉴权
		AuthorizationUtil.userAuth(groups);
		// 查询列表
		long num =  msgService.countUserUnread(app, kongUserId,categoryId);
		MsgCountInfo info = new MsgCountInfo();
		info.setUnread(num);
		return info;
	}

	/**
	 * 用户查询自己的消息已读/未读数目
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午1:43:23
	 *
	 * @param groups
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public MsgCountInfo count(//
			@RequestHeader(value = "X-Consumer-Groups" , required = false) String groups, // user
			@RequestHeader(value = "X-Consumer-Custom-ID", required = false) String kongUserId, // 用户的唯一ID
			@RequestParam(value = "app", required = true, defaultValue = "c-f") String app) {// app标识
		// 用户鉴权
		AuthorizationUtil.userAuth(groups);
		// 查询列表
		return msgService.count(app, kongUserId);
	}

	/**
	 * 根据类目用户查询自己的消息已读/未读数目
	 * [类目]
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午1:43:23
	 *
	 * @param groups
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/count/category", method = RequestMethod.GET)
	public List<MsgCountInfo> countInfoByCategory(//
			@RequestHeader(value = "X-Consumer-Groups" , required = false) String groups, // user
			@RequestHeader(value = "X-Consumer-Custom-ID", required = false) String kongUserId, // 用户的唯一ID
			@RequestParam(value = "app", required = true, defaultValue = "c-f") String app){// app标识
		// 用户鉴权
		AuthorizationUtil.userAuth(groups);
		// 查询列表
		return null;
	}

	/**
	 * 读取消息
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月8日下午4:56:59
	 *
	 * @param groups
	 * @param userId
	 * @param msgId
	 * @return
	 */
	@RequestMapping(value = "/{msgId}", method = RequestMethod.GET)
	public Message read(// 读取消息
			@RequestHeader(value = "X-Consumer-Groups" , required = false) String groups, // user
			@RequestHeader(value = "X-Consumer-Custom-ID", required = false) String kongUserId, // 用户的唯一ID
			@PathVariable(value = "msgId") String msgId) {// Id
		// 管理员
		if (AuthorizationUtil.checkAdminAuth()) {
			return msgService.read(msgId);
		}
		//用户
		AuthorizationUtil.userAuth(groups);
		Message message =  msgService.read(kongUserId, msgId);
		return message;
	}

	/**
	 * 删除文章
	 * 
	 * @param groups
	 * @param postIds
	 */
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public void delete(// 删除用户消息
			@RequestHeader(value = "X-Consumer-Groups" , required = false) String groups, // user
			@RequestHeader(value = "X-Consumer-Custom-ID", required = false) String kongUserId, // 用户的唯一ID
			@RequestParam(value = "msgIds", required = true) String msgIds) {// 消息Id,以'，'分割
		// 管理员
		if (AuthorizationUtil.checkAdminAuth()) {
			msgService.delete(msgIds);
			return;
		}
		// 用户
		AuthorizationUtil.userAuth(groups);
		msgService.delete(kongUserId, msgIds);
	}
	
	
	/**
	 * 通知读取消息
	 * 
	 * @param groups
	 * @param postIds
	 */
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	public void notifyRead(// 删除用户消息
			@RequestHeader(value = "X-Consumer-Groups" , required = false) String groups, // user
			@RequestHeader(value = "X-Consumer-Custom-ID", required = false) String userId, // 用户的唯一ID
			@RequestParam(value = "app", required = false, defaultValue = "c-f") String app,
			@RequestParam(value = "categoryId", required = false) String categoryId, // 类目标示
			@RequestParam(value = "ids", required = false) String ids) {// 消息Id,以'，'分割
		// 用户
		AuthorizationUtil.userAuth(groups);
		if (!StringUtils.isEmpty(ids)) {
			msgService.notifyRead(userId,ids);
		}else{
			msgService.notifyReadCategory(userId,app,categoryId);
		}
	}

}
