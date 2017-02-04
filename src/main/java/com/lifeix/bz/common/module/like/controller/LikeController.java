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
@RequestMapping(value = "/likes")
public class LikeController {

	@Autowired
	private LikeService likeService;
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Like addLike(@RequestParam(value = "type", required = true) String type, @RequestParam(value = "target", required = true) String target,
			@RequestParam(value = "like", required = true) Boolean like) {
		String source = getSource();
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		String newType = getNewType(type, target);
		Like result = likeService.addLike(source, newType, target, like);
		return result;
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Like> getGroupLikes(@RequestParam(value = "group", required = true) String group) {
		String source = getSource();
		List<Like> list = likeService.getGroupLikes(group, source);
		return list;
	}

	@RequestMapping(value = "/{target}", method = RequestMethod.GET)
	public Like getTargetLiked(@PathVariable(value = "target") String target, @RequestParam(value = "type", required = true) String type) {
		String source = getSource();
		String newType = getNewType(type, target);
		return likeService.getLike(source, newType, target);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Like getLike(@RequestParam(value = "target", required = true) String target, @RequestParam(value = "type", required = true) String type) {
		String source = getSource();
		String newType = getNewType(type, target);
		return likeService.getLike(source, newType, target);
	}
	
	private String getSource(){
		// 暂时放开ip like
		String source = "random"+System.currentTimeMillis();
//		String source = IpUtil.getIpAddr(BaseApi.getCurrentRequest());
		return source;
	}
	
	/**
	 * 客户端类型传错，妥协在此处获得郭炳颜的比喜欢数据
	 * 曾光伟
	 */
	private String getNewType(String type,String target){
		if ("staffs".equals(type) && "11".equals(target)) {
			return "leader";
		}
		return type;
	}
	
}
