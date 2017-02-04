package com.lifeix.test.bz.common.comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lifeix.bz.common.module.comment.model.Comment;
import com.lifeix.bz.common.module.comment.model.CommonComment;
import com.lifeix.bz.common.module.comment.model.Count;
import com.lifeix.bz.common.module.comment.model.Reply;
import com.lifeix.bz.common.module.comment.model.SubjectComment;
import com.lifeix.football.common.util.HttpUtil;
import com.lifeix.football.common.util.JSONUtils;

public class CommentApiTest {

	private String env = "dev";

	private String HOST;

	// zenggw
	private String 	KEY1 = "e9841fd8246e4591ba9abf6fb2450796";
	private String User1ID = "d2b7274e958eb26a7666fd625c5203b8";

	// 亚林
	private String KEY2 = "d48ca6dddb7b47928c30c4e95dfc02ff";
	private String User2ID = "58328b17e4b0e1110ef19891";

	@org.junit.Before
	public void init() {
		if (env.equals("production")) {
			HOST = "http://api.c-f.com";
			
			//zengguangwei
			User1ID ="580a23afe4b03061634143f9";
			KEY1="6bc8c78f9374453b83d36087a560bc64";
			
			//15110261173
			User2ID ="5854a266e4b0d92630f860fe";
			KEY2="6915a989ac2d439d8b9a3ec8558998b8";
		}else if (env.equals("production2")) {
			HOST = "http://114.55.134.143:8300";
			//zengguangwei
			User1ID ="580a23afe4b03061634143f9";
			KEY1="6bc8c78f9374453b83d36087a560bc64";
			//15110261173
			User2ID ="5854a266e4b0d92630f860fe";
			KEY2="6915a989ac2d439d8b9a3ec8558998b8";
			
		} else if (env.equals("qa")) {
			HOST = "http://54.223.127.33:8000";
		} else if (env.equals("dev")) {
			HOST = "http://127.0.0.1:8080";
		}
		System.out.println(HOST);
	}
	

	@Test
	public void addComment() throws Exception{
		String subjectType = "decision";
		Comment comment1 = addComment(KEY1, User1ID, subjectType, "5816ba579b8ef3cf35bee582", "", String.valueOf("qibin哈哈哈 "+System.currentTimeMillis()));
		System.out.println(JSONUtils.obj2json(comment1));
//		Comment comment2 = addComment(KEY1, User1ID, "decision", "5816ba579b8ef3cf35bee582", "", String.valueOf("这是 评论2 "+System.currentTimeMillis()));
//		Comment comment3 = addComment(KEY1, User1ID, "decision", "5816ba579b8ef3cf35bee582", "", String.valueOf("这是 评论3 "+System.currentTimeMillis()));
//		Comment comment4 = addComment(KEY1, User1ID, "decision", "5816ba579b8ef3cf35bee582", "", String.valueOf("这是 评论4 "+System.currentTimeMillis()));
	}
	
	@Test
	public void queryDecision() throws Exception{
		String sendGet = HttpUtil.sendGet(HOST+"/football/appgateway/decision/getDecisionItems?categoryId=2016-csl&roundId=2016-csl-29&key=visitor");
		System.out.println(sendGet);
		if (StringUtils.isEmpty(sendGet)) {
			Assert.fail();
		}
		JSONArray parseArray = JSONArray.parseArray(sendGet);
		if (parseArray==null||parseArray.size()<1) {
			Assert.fail();
		}
	}
	
	@Test
	public void test() throws Exception {
		String subjectType = String.valueOf(System.currentTimeMillis());
		String subjectId = "21";
		List<Comment> comments = null;
		Map<String, SubjectComment> subjects = null;
		/**
		 * 用户1对subjectId1发评论
		 */
		String images1 = "";
		String content1 = " 用户1对subjectId1发评论1";
		Comment comment1 = addComment(KEY1, User1ID, subjectType, subjectId, images1, content1);
		assertComment(comment1);

		List<Count> counts = countComment(KEY1, subjectType, subjectId);
		Assert.assertEquals(1l, counts.get(0).getNum());

		/**
		 * 点赞
		 */
		addLike(KEY1, User1ID, comment1.getId());
		/**
		 * 重复点赞
		 */
		addLike(KEY1, User1ID, comment1.getId());
		/**
		 * 添加回复1
		 */
		Reply reply = addReply(KEY2, User2ID, comment1.getId(), "", "这是对1号评论的回复");
		// 返回的Reply中包含reply且comment.count=1
		comments = getSubjectComments(KEY1, User1ID, subjectType, subjectId, 100, null, true, 5);
		Comment comment = comments.get(0);
		Assert.assertEquals(1, comment.getReplySum());
		assertComment(comment, comment1);
		List<Reply> replies = comment.getReplies();
		Assert.assertNotNull(replies);
		assertRepley(reply, replies.get(0));
		// 测试like数目
		Assert.assertEquals(1, comment.getLikeNum());
		Assert.assertEquals(true, comment.isLike());
		/**
		 * 删除like
		 */
		deleteLike(KEY1, User1ID, comment1.getId());
		comments = getSubjectComments(KEY1, User1ID, subjectType, subjectId, 100, null, true, 5);
		comment = comments.get(0);
		Assert.assertEquals(0, comment.getLikeNum());
		Assert.assertEquals(false, comment.isLike());
		/**
		 * 测试重复删除like
		 */
		deleteLike(KEY1, User1ID, comment1.getId());
		comments = getSubjectComments(KEY1, User1ID, subjectType, subjectId, 100, null, true, 5);
		comment = comments.get(0);
		Assert.assertEquals(0, comment.getLikeNum());
		/**
		 * 通过listreply接口返回复
		 */
		replies = listReplies(KEY1, comment.getId());
		Assert.assertNotNull(replies);
		assertRepley(reply, replies.get(0));
		/**
		 * 删除回复
		 */
		deleteReply(KEY2, User2ID, reply.getId());
		//// 返回的Reply中不包含reply且comment.count=0
		comments = getSubjectComments(KEY1, User1ID, subjectType, subjectId, 100, null, true, 5);
		comment = comments.get(0);
		Assert.assertEquals(0, comment.getReplySum());
		replies = comment.getReplies();
		if (!CollectionUtils.isEmpty(replies)) {
			Assert.fail();
		}

		subjects = getCommentMapByMutiSubjects(KEY1, User1ID, subjectType, subjectId, 100);
		assertComment(subjects.get(subjectId).getComments().get(0), comment1);
		/**
		 * 用户1对subjectId1下的评论1发回复
		 */
		String images2 = "";
		String content2 = " 用户1对subjectId1发评论2";
		Comment comment2 = addComment(KEY1, User1ID, subjectType, subjectId, images2, content2);
		assertComment(comment2);

		counts = countComment(KEY1, subjectType, subjectId);
		Assert.assertEquals(2l, counts.get(0).getNum());

		/**
		 * 查看评论
		 */
		comments = getSubjectComments(KEY1, User1ID, subjectType, subjectId, 100, null, true, 5);
		assertComment(comments.get(0), comment2);
		assertComment(comments.get(1), comment1);

		subjects = getCommentMapByMutiSubjects(KEY1, User1ID, subjectType, subjectId, 100);
		assertComment(subjects.get(subjectId).getComments().get(0), comment2);
		assertComment(subjects.get(subjectId).getComments().get(1), comment1);
		/**
		 * 删除第一条评论
		 */
		deleteComment(KEY1, comments.get(0).getId());
		subjects = getCommentMapByMutiSubjects(KEY1, User1ID, subjectType, subjectId, 100);
		assertComment(subjects.get(subjectId).getComments().get(0), comment1);
		Assert.assertEquals(1, subjects.get(subjectId).getComments().size());

		comments = getSubjectComments(KEY1, User1ID, subjectType, subjectId, 100, new Date(), true, 5);
		Assert.assertEquals(1, comments.size());
		assertComment(comments.get(0), comment1);
		// 测试数目
		counts = countComment(KEY1, subjectType, subjectId);
		Assert.assertEquals(1l, counts.get(0).getNum());
		/**
		 * 删除第二条评论
		 */
		deleteComment(KEY1, comments.get(0).getId());
		comments = getSubjectComments(KEY1, User1ID, subjectType, subjectId, 100, new Date(), true, 5);
		Assert.assertEquals(0, comments.size());
		// 测试数目
		counts = countComment(KEY1, subjectType, subjectId);
		if (!CollectionUtils.isEmpty(counts)) {
			Assert.fail();
		}
		//
		subjects = getCommentMapByMutiSubjects(KEY1, User1ID, subjectType, subjectId, 100);	
		if (!CollectionUtils.isEmpty(subjects.get(subjectId).getComments())) {
			Assert.fail();
		}
	}
	
	
	private List<Count> countComment(String key, String subjectType, String subjectIds) throws Exception {
		String sendGet = HttpUtil.sendGet(HOST + "/football/comment/comments/count?key=" + key + "&subjectType=" + subjectType + "&subjectIds=" + subjectIds);
		if (StringUtils.isEmpty(sendGet)) {
			return new ArrayList<>();
		}
		return JSONObject.parseArray(sendGet, Count.class);
	}

	private void deleteLike(String key, String user1id2, String id) throws Exception {
		HttpUtil.sendDelete(HOST + "/football/comment/likes?key=" + key + "&commentId=" + id, getHeader(user1id2));
	}

	private void addLike(String key, String user1id2, String id) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("commentId", id);
		HttpUtil.sendPost(HOST + "/football/comment/likes/?key=" + key, getHeader(user1id2), params);
	}

	private List<Reply> listReplies(String key, String commentId) throws Exception {
		String sendGet = HttpUtil.sendGet(HOST + "/football/comment/replies?key=" + key + "&commentId=" + commentId);
		if (StringUtils.isEmpty(sendGet)) {
			return new ArrayList<>();
		}
		return JSONObject.parseArray(sendGet, Reply.class);
	}

	private void deleteReply(String key, String userId, String id) throws Exception {
		String url = HOST + "/football/comment/replies/" + id + "?key=" + key;
		System.out.println(url);
		HttpUtil.sendDelete(url, getHeader(userId));
	}

	private Reply addReply(String key, String userId, String commentId, String images, String content) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("images", images);
		params.put("content", content);
		params.put("commentId", commentId);
		String result = HttpUtil.sendPost(HOST + "/football/comment/replies?key=" + key, getHeader(userId), params);
		Reply comment = JSONObject.parseObject(result, Reply.class);
		return comment;
	}

	private void deleteComment(String kEY1, String id) throws Exception {
		HttpUtil.sendDelete(HOST + "/football/comment/comments/" + id + "?key=" + kEY1);
	}

	private Map<String, SubjectComment> getCommentMapByMutiSubjects(String kEY1, String userId, String subjectType, String subjectId, int limit) throws Exception {
		String url = 
				HOST + "/football/comment/comments/map?key=" + kEY1 + "&subjectType=" + subjectType + "&subjectIds=" + subjectId + "&limit=" + limit;
		String sendGet = HttpUtil.sendGet(url,getHeader(userId));
		System.out.println(url);
		if (StringUtils.isEmpty(sendGet)) {
			return new HashMap<>();
		}
		System.out.println(sendGet);
		return JSONUtils.json2map(sendGet, SubjectComment.class);
	}

	private List<Comment> getSubjectComments(String kEY1, String userId, String subjectType, String subjectId, int limit, Date date, boolean containReply, int replyLimit)
			throws Exception {
		String sendGet = HttpUtil.sendGet(HOST + "/football/comment/comments?key=" + kEY1 + "&subjectType=" + subjectType + "&subjectId=" + subjectId + "&limit=" + limit
				+ "&containReply=" + containReply + "&replyLimit=" + replyLimit, getHeader(userId));
		if (StringUtils.isEmpty(sendGet)) {
			return new ArrayList<>();
		}
		return JSONObject.parseArray(sendGet, Comment.class);
	}

	private Comment addComment(String key, String userId, String subjectType, String subjectId, String images, String content) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("subjectType", subjectType);
		params.put("subjectId", subjectId);
		params.put("images", images);
		params.put("content", content);
		String result = HttpUtil.sendPost(HOST + "/football/comment/comments?key=" + key, getHeader(userId), params);
		Comment comment = JSONObject.parseObject(result, Comment.class);
		return comment;
	}

	private Map<String, String> getHeader(String userId) {
		Map<String, String> map = new HashMap<>();
		if (!env.equals("production")) {
			map.put("X-Consumer-Groups", "user");
			map.put("X-Consumer-Custom-ID", userId);
		}
		map.put("Content-Type", "application/x-www-form-urlencoded");
		return map;
	}

	private void assertComment(Comment comment1, Comment comment2) {
		assertComment(comment1);
		assertComment(comment2);
		Assert.assertEquals(comment1.getContent(), comment2.getContent());
		Assert.assertEquals(comment1.getId(), comment2.getId());
	}

	private void assertComment(CommonComment comment) {
		Assert.assertNotNull(comment);
		Assert.assertNotNull(comment.getId());
		Assert.assertNotNull(comment.getCreateTime());
		Assert.assertNotNull(comment.getContent());
	}

	private void assertRepley(Reply reply, Reply reply1) {
		assertComment(reply);
		assertComment(reply1);
		Assert.assertEquals(reply.getContent(), reply1.getContent());
		Assert.assertEquals(reply.getId(), reply1.getId());
		Assert.assertEquals(reply.getCommentId(), reply1.getCommentId());
	}

	private void assertComments(List<Comment> comments, List<Comment> asList) {
		for (int i = 0; i < comments.size(); i++) {
			assertComment(comments.get(i), asList.get(i));
		}
	}

}
