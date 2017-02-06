package com.lifeix.bz.common.module.like.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.bz.common.module.like.model.Like;
import com.lifeix.bz.common.module.like.service.LikeService;

@RestController
@RequestMapping(value = "/like/likes")
public class LikeController {

	@Autowired
	private LikeService likeService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	public Like addLike(@RequestParam(value = "source", required = false) String source, @RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "target", required = true) String target, @RequestParam(value = "like", required = true) Boolean like) {
		String newSource = source;
		if (StringUtils.isEmpty(newSource)) {
			newSource = getSource();
		}
		Like result = likeService.addLike(source, type, target, like);
		return result;
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Like> getGroupLikes(@RequestParam(value = "source", required = false) String source, @RequestParam(value = "group", required = true) String group) {
		String newSource = source;
		if (StringUtils.isEmpty(newSource)) {
			newSource = getSource();
		}
		List<Like> list = likeService.getGroupLikes(group, newSource);
		return list;
	}

	/**
	 * 获得单个对象喜欢情况
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月4日下午1:58:23
	 *
	 * @param target
	 * @param type
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/{target}", method = RequestMethod.GET)
	public Like getTargetLiked(@PathVariable(value = "target") String target, @RequestParam(value = "source", required = false) String source,
			@RequestParam(value = "type", required = true) String type) {
		String newSource = source;
		if (StringUtils.isEmpty(newSource)) {
			newSource = getSource();
		}
		return likeService.getLike(newSource, type, target);
	}

	/**
	 * 获得单个对象喜欢情况
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月4日下午1:58:23
	 *
	 * @param target
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Like getLike(@RequestParam(value = "source", required = false) String source, @RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "target", required = true) String target) {
		String newSource = source;
		if (StringUtils.isEmpty(newSource)) {
			newSource = getSource();
		}
		return likeService.getLike(newSource, type, target);
	}

	/**
	 * 给其一个随机的来源，保证所有的点赞都能进行
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月4日下午2:14:48
	 *
	 * @return
	 */
	private String getSource() {
		// 暂时放开ip like
		String source = "random" + System.currentTimeMillis();
		// String source = IpUtil.getIpAddr(BaseApi.getCurrentRequest());
		return source;
	}

}
