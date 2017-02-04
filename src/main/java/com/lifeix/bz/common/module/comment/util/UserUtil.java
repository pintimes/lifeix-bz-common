package com.lifeix.bz.common.module.comment.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.lifeix.football.common.model.User;
import com.lifeix.football.common.util.HttpUtil;

public class UserUtil {

	private static Logger logger = LoggerFactory.getLogger(UserUtil.class);

	/**
	 * 从用户中心获得用户信息
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月8日上午9:43:35
	 *
	 * @param id
	 * @return
	 */
	public static User getUser(String host, String port, String id) {
		logger.debug("getUser host={},port={},id={}", host, port, id);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("http://").append(host).append(":").append(port).append("/football/user/users/").append(id).append("/base");
			String path = sb.toString();
			String result = HttpUtil.sendGet(path);
			if (StringUtils.isEmpty(result)) {
				return null;
			}
			return JSONObject.parseObject(result, User.class);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
