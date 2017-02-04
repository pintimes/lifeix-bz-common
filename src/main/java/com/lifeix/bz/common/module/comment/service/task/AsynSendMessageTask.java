package com.lifeix.bz.common.module.comment.service.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lifeix.bz.common.module.comment.service.impl.CommentService;
import com.lifeix.football.common.model.Message;

/**
 * 异步消息
 * @author zengguangwei
 *
 */
@Service
public class AsynSendMessageTask {

	private Logger logger = LoggerFactory.getLogger(CommentService.class);
	
	/**
	 * 发送消息
	 * @description
	 * @author zengguangwei 
	 * @version 2016年11月7日下午4:56:09
	 *
	 * @param ip
	 * @param port
	 * @param targetType
	 * @param user
	 * @param po
	 */
	@Async
	public void sendMsg(String ip,String port, Message msg) {
		logger.info("sendMessage start");
		// 发送消息
		try {
			sendMessage(ip, port, msg);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		logger.info("sendMessage end");
	}
	

	/**
	 * 发送消息
	 * 如果网络断开或者发送失败则抛出异常
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月7日下午4:15:58
	 *
	 * @param msg
	 */
	public static void sendMessage(String ip, String port, Message msg) {
	}

}
