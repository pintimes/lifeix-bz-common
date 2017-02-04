package com.lifeix.bz.common.module.comment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lifeix.bz.common.module.comment.CommentConfig;
import com.lifeix.bz.common.module.comment.model.SubjectComment;
import com.lifeix.bz.common.module.comment.service.impl.CommentService;
import com.lifeix.football.common.util.HttpUtil;

@RestController
@RequestMapping(value = "/appgateway")
/**
 * 临时放在此处
 * @author zengguangwei
 *
 */
public class DecisonController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentConfig appConfig;

	private Logger logger = LoggerFactory.getLogger(CommentService.class);

	//http://api.c-f.com/football/comment/comments?subjectType=decision&subjectId=5816ba579b8ef3cf35bee582&key=visitor
	//http://54.223.127.33:8000/football/appgateway/decision/getDecisionItems?categoryId=2016-csl&roundId=2016-csl-29&key=visitor
	@RequestMapping(value = "/decision/getDecisionItems", method = RequestMethod.GET)
	public JSONArray getDecision(
			@RequestHeader(required = false, value = "X-Consumer-Groups") String groups, // user
			@RequestHeader(required = false, value = "X-Consumer-Custom-ID") String userId, // 用户的唯一ID
			@RequestParam(value = "roundId", required = true) String roundId, //
			@RequestParam(value = "containComment", required = false, defaultValue = "true") boolean containComment, // 是否包含回复
			@RequestParam(value = "commentLimit", required = false, defaultValue = "3") int commentLimit) {// 回复的数目
		try {
			String decisionhost = appConfig.getDecisionhost();
			String decisionport = appConfig.getDecisionport();
			String url ="http://" + decisionhost + ":" + decisionport + "/football/decision/items?roundId="+roundId;
			Map<String, String> hearder = new HashMap<>();
			hearder.put("X-Consumer-Custom-ID", userId);
			hearder.put("X-Consumer-Groups", groups);
			String sendGet = HttpUtil.sendGet(url,hearder);
			if (StringUtils.isEmpty(sendGet)) {
				return new JSONArray();
			}
			JSONObject json = JSONObject.parseObject(sendGet);
			JSONArray items = json.getJSONArray("data");
			if (items == null || items.size() == 0) {
				return new JSONArray();
			}
			/**
			 * 组装评论
			 */
			List<String> ids = new ArrayList<>();
			for (int i = 0, size = items.size(); i < size; i++) {
				JSONObject decision = items.getJSONObject(i);
				if (decision == null) {
					continue;
				}
				String id = decision.getString("id");
				if (StringUtils.isEmpty(id)) {
					continue;
				}
				ids.add(id);
			}
			Map<String, SubjectComment> map = commentService.getCommentMapByMutiSubjects("decision", ids, 3);
			if (CollectionUtils.isEmpty(map)) {
				return items;
			}
			for (int i = 0, size = items.size(); i < size; i++) {
				JSONObject decision = items.getJSONObject(i);
				if (decision == null) {
					continue;
				}
				String id = decision.getString("id");
				if (StringUtils.isEmpty(id)) {
					continue;
				}
				decision.put("comment", map.get(id));
			}
			return items;
		} catch (Exception e) {
			logger.error("Decision Error", e);
		}
		return new JSONArray();
	}

}
