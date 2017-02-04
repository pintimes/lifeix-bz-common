package com.lifeix.bz.common.module.like.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.lifeix.bz.common.module.like.dao.LikeDao.LikeRecord;
import com.lifeix.bz.common.module.like.model.Like;

public class LikeUtil {

	public static String createLikeRecordId(String type,String source,String target){
		return type + "#" + source + "#" + target;
	}

	public static List<Like> getLike(String type,List<LikeRecord> result) {
		if (CollectionUtils.isEmpty(result)) {
			return null;
		}
		List<Like> likes = new ArrayList<>();
		for (LikeRecord likeRecord : result) {
			Like like = new Like();
			like.setType(type);
			like.setTarget(likeRecord.getTarget());
			like.setLikeNum(likeRecord.getLikeNum());
			like.setUnlikeNum(likeRecord.getUnlikeNum());
			likes.add(like);
		}
		return likes;
	}

}
