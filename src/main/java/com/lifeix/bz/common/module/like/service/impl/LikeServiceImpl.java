package com.lifeix.bz.common.module.like.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.lifeix.bz.common.module.like.dao.LikeDao;
import com.lifeix.bz.common.module.like.dao.LikeGroupDao;
import com.lifeix.bz.common.module.like.dao.TargetLikedDao;
import com.lifeix.bz.common.module.like.model.Like;
import com.lifeix.bz.common.module.like.po.LikeGroupPO;
import com.lifeix.bz.common.module.like.po.LikePO;
import com.lifeix.bz.common.module.like.po.TargetLikedPO;
import com.lifeix.bz.common.module.like.service.LikeService;
import com.lifeix.bz.common.module.like.util.LikeUtil;
import com.lifeix.football.common.util.AdapterUtil;

@Service
public class LikeServiceImpl implements LikeService {

	private Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

	@Autowired
	private LikeDao likeDao;

	@Autowired
	private LikeGroupDao likeGroupDao;
	
	@Autowired
	private TargetLikedDao targetLikedDao;

	/**
	 * 当加入一条记录的时候将key=#like.type的缓存删除， 这样下次读取的时候会重新从数据库读取
	 */
	@Override
	public Like addLike(String source, String type, String target, boolean like) {
		if (StringUtils.isEmpty(type)) {
			return null;
		}
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		if (StringUtils.isEmpty(target)) {
			return null;
		}
		/**
		 * 主键生成策略，避免重复
		 */
		String id = LikeUtil.createLikeRecordId(type, source, target);
		LikePO po = likeDao.findById(id);
		if (po == null) {
			po = new LikePO();
			po.setCreateTime(new Date());
			po.setType(type);
			po.setSource(source);
			po.setTarget(target);
			po.setLike(like);
			po.setId(id);
			likeDao.save(po);
			/**
			 * target 喜欢数目加，并且通知缓存
			 */
			if (like) {
				addTargetLike(type, target);
			} else {
				addTargetUnLike(type, target);
			}
		}
		Like dto = AdapterUtil.toT(po, Like.class);
		return dto;
	}

	@Override
	public List<Like> getGroupLikes(String group, String source) {
		logger.info("getGroupLikes read from db");
		/**
		 * 获得Group的喜欢记录
		 */
		List<Like> likes = getGroupLikes(group);
		if (CollectionUtils.isEmpty(likes)) {
			return likes;
		}
		/**
		 * 获得用户的喜欢列表
		 */
		if (StringUtils.isEmpty(source)) {
			return likes;
		}
		/**
		 * 获得用户喜欢记录的Id
		 */
		List<String> pids = new ArrayList<>();
		for (Like like : likes) {
			String pid = LikeUtil.createLikeRecordId(like.getType(), source, like.getTarget());
			pids.add(pid);
		}
		/**
		 * 根据Id找到喜欢记录
		 */
		List<LikePO> temps = likeDao.findByIds(pids);
		/**
		 * 如果用户未喜欢过Target则直接返回
		 */
		if (CollectionUtils.isEmpty(temps)) {
			return likes;
		}
		/**
		 * 设置是否喜欢过
		 */
		Map<String, LikePO> map = new HashMap<>();
		for (LikePO po : temps) {
			map.put(po.getTarget(), po);
		}
		for (Like like : likes) {
			LikePO po = map.get(like.getTarget());
			if (po == null) {
				continue;
			}
			like.setLike(po.isLike());
		}
		return likes;
	}

	@Override
	public Like getLike(String source, String type, String target) {
		if (StringUtils.isEmpty(type)) {
			return null;
		}
		if (StringUtils.isEmpty(target)) {
			return null;
		}
		/**
		 * 获得某类型某target的喜欢情况
		 */
		Like like = getLikes(type, target);
		/**
		 * 如果喜欢数=0，则source也未喜欢过此target
		 */
		if (like.getLikeNum()==0&&like.getUnlikeNum()==0) {
			return like;
		}
		/**
		 * 如果source=null，即我不关心source是否喜欢过这个target
		 */
		if (StringUtils.isEmpty(source)) {
			return like;
		}
		LikePO likepo = likeDao.getSourceLikes(type, source, target);
		/**
		 * 当前source未喜欢过/踩过这个target
		 */
		if (likepo == null) {
			return like;
		}
		like.setLike(likepo.isLike());
		return like;
	}
	
	private void addTargetLike(String type, String target) {
		/**
		 * 增加Target liked
		 */
		String targetId = type + "#" + target;
		targetLikedDao.increaseLike(targetId);
	}

	private void addTargetUnLike(String type, String target) {
		/**
		 * 增加Target liked
		 */
		String targetId = type + "#" + target;
		targetLikedDao.increaseUnLike(targetId);
	}

	private Like getLikes(String type, String target) {
		logger.info("从数据库中获取 type={},target={}", type, target);
		String id = type + "#" + target;
		TargetLikedPO likeRecord = targetLikedDao.findById(id);
		/**
		 * 组装成Like对象
		 */
		Like like = new Like();
		like.setTarget(target);
		like.setType(type);
		if (likeRecord == null) {
			like.setLikeNum(0);
			like.setUnlikeNum(0);
			like.setLike(null);
			return like;
		}
		like.setLikeNum(likeRecord.getLikeNum());
		like.setUnlikeNum(likeRecord.getUnlikeNum());
		return like;
	}

	private List<Like> getGroupLikes(String group) {
		LikeGroupPO groupPO = likeGroupDao.findById(group);
		if (groupPO == null || CollectionUtils.isEmpty(groupPO.getTargets())) {
			return null;
		}
		List<String> ids = groupPO.getTargets();
		logger.info("getGroupLikes read from db");
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		List<TargetLikedPO> pos = targetLikedDao.findByIds(ids);
		if (CollectionUtils.isEmpty(pos)) {
			return null;
		}
		List<Like> likes = new ArrayList<>();
		for (TargetLikedPO targetLikedPO : pos) {
			String id = targetLikedPO.getId();
			String[] infos = id.split("#");
			String type = infos[0];
			String target = infos[1];
			// 组装
			Like like = new Like();
			like.setType(type);
			like.setTarget(target);
			like.setLikeNum(targetLikedPO.getLikeNum());
			like.setUnlikeNum(targetLikedPO.getUnlikeNum());
			likes.add(like);
		}
		return likes;
	}

}
