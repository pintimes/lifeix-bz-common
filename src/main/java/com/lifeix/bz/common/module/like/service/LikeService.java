package com.lifeix.bz.common.module.like.service;

import java.util.List;

import com.lifeix.bz.common.module.like.model.Like;

public interface LikeService {

	/**
	 * source lik target
	 * 
	 * @param like
	 * @return
	 */
	public Like addLike(String source, String type, String target, boolean like);

	/**
	 * 获得source的所有Target的喜欢情况
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年5月31日下午2:40:54
	 *
	 * @param type
	 * @param source
	 * @param taget
	 * @return
	 */
	public Like getLike(String source, String type, String target);

	
	/**
	 * 获得一组target的喜欢数据
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年6月30日下午3:36:34
	 *
	 * @return
	 */
	public List<Like> getGroupLikes(String group,String source);
	
}
