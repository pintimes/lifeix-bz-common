package com.lifeix.bz.common.module.like.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lifeix.football.common.util.HttpUtil;

/**
 * 获取点赞的分组信息
 * 
 * @author zengguangwei
 *
 */
public class GroupUtil {

	public static List<String> getNationalCoachIds(String apigateway) {
		try {
			String result = HttpUtil.sendGet(apigateway + "/football/games/coaches/national?key=visitor");
			if (StringUtils.isEmpty(result)) {
				return null;
			}
			List<String> ids = new ArrayList<>();
			JSONArray tops = JSON.parseArray(result);// CoachCategoryTopList
			for (int i = 0; i < tops.size(); i++) {
				JSONObject top = tops.getJSONObject(i);
				if (top == null) {
					continue;
				}
				JSONArray categories = top.getJSONArray("category");
				if (categories == null) {
					continue;
				}
				for (int j = 0; j < categories.size(); j++) {
					JSONObject category = categories.getJSONObject(j);
					JSONArray coaches = category.getJSONArray("coaches");
					if (coaches == null) {
						continue;
					}
					for (int k = 0; k < coaches.size(); k++) {
						JSONObject coach = coaches.getJSONObject(k);
						if (coach == null) {
							continue;
						}
						String id = coach.getString("id");
						if (StringUtils.isEmpty(id)) {
							continue;
						}
						ids.add("coach#" + id);
					}
				}
			}
			return ids;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, List<String>> getNationalPlayerIds(String apigateway) {
		try {
			String result = HttpUtil.sendGet(apigateway + "/football/games/players/national?key=visitor");
			if (StringUtils.isEmpty(result)) {
				return null;
			}
			Map<String, List<String>> map = new HashMap<>();
			JSONArray tops = JSON.parseArray(result);// CoachCategoryTopList
			for (int i = 0; i < tops.size(); i++) {
				JSONObject top = tops.getJSONObject(i);
				if (top == null||!top.containsKey("category") || !top.containsKey("topName")) {
					continue;
				}
				List<String> ids = new ArrayList<>();
				String topName = top.getString("topName");
				JSONArray categories = top.getJSONArray("category");
				for (int j = 0; j < categories.size(); j++) {
					JSONObject category = categories.getJSONObject(j);
					String categoryName = category.getString("categoryName");
					JSONArray palyers = category.getJSONArray("players");
					if (palyers == null) {
						continue;
					}
					for (int k = 0; k < palyers.size(); k++) {
						JSONObject player = palyers.getJSONObject(k);
						if (player == null) {
							continue;
						}
						String id = player.getString("id");
						if (StringUtils.isEmpty(id)) {
							continue;
						}
						ids.add("player#" + id);
					}
					map.put("player"+topName+categoryName, ids);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getFIFARefereeIds(String apigateway) {
		try {
			String result = HttpUtil.sendGet(apigateway + "/football/games/referees?key=visitor&level=FIFA");
			if (StringUtils.isEmpty(result)) {
				return null;
			}
			List<String> ids = new ArrayList<>();
			JSONArray tops = JSON.parseArray(result);// CoachCategoryTopList
			for (int i = 0; i < tops.size(); i++) {
				JSONObject top = tops.getJSONObject(i);
				if (top == null) {
					continue;
				}
				JSONArray categories = top.getJSONArray("category");
				if (categories == null) {
					continue;
				}
				for (int j = 0; j < categories.size(); j++) {
					JSONObject category = categories.getJSONObject(j);
					JSONArray referees = category.getJSONArray("referees");
					if (referees == null) {
						continue;
					}
					for (int k = 0; k < referees.size(); k++) {
						JSONObject referee = referees.getJSONObject(k);
						if (referee == null) {
							continue;
						}
						String id = referee.getString("id");
						if (StringUtils.isEmpty(id)) {
							continue;
						}
						ids.add("referee#" + id);
					}
				}
			}
			return ids;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Map<String, List<String>>  getLeagueRefereeIds(String apigateway){
		String result;
		try {
			result = HttpUtil.sendGet(apigateway + "/football/games/referees/leagueCategory?key=visitor");
			if (StringUtils.isEmpty(result)) {
				return null;
			}
			JSONArray tops = JSON.parseArray(result);// CoachCategoryTopList
			if (tops == null || tops.size()==0) {
				return null ;
			}
			Map<String, List<String>> map = new HashMap<>();
			for (int i = 0; i < tops.size(); i++) {
				String name = tops.getString(i);
				List<String> list = getLeagueRefereeIds(apigateway,name);
				if (CollectionUtils.isEmpty(list)) {
					continue;
				}
				map.put(name, list);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<String> getLeagueRefereeIds(String apigateway,String name) {
		try {
			String result = HttpUtil.sendGet(apigateway + "/football/games/referees/league?key=visitor&league="+name);
			if (StringUtils.isEmpty(result)) {
				return null;
			}
			List<String> ids = new ArrayList<>();
			JSONArray tops = JSON.parseArray(result);// CoachCategoryTopList
			for (int i = 0; i < tops.size(); i++) {
				JSONObject top = tops.getJSONObject(i);
				if (top == null) {
					continue;
				}
				JSONArray referees = top.getJSONArray("referees");
				if (referees == null) {
					continue;
				}
				for (int k = 0; k < referees.size(); k++) {
					JSONObject referee = referees.getJSONObject(k);
					if (referee == null) {
						continue;
					}
					String id = referee.getString("id");
					if (StringUtils.isEmpty(id)) {
						continue;
					}
					ids.add("referee#" + id);
				}
			}
			return ids;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getNationalTeam(String apigateway) {
		try {
			String result = HttpUtil.sendGet(apigateway + "/football/games/competitions/5/teams/1/competitionTeam?key=visitor");
			if (StringUtils.isEmpty(result)) {
				return null;
			}
			List<String> ids = new ArrayList<>();
			JSONObject json = JSON.parseObject(result);// CoachCategoryTopList
			
			JSONObject chiefCoach = json.getJSONObject("chiefCoach");
			if (chiefCoach!=null &&chiefCoach.containsKey("id")) {
				ids.add("coach#"+chiefCoach.getString("id"));
			}
			JSONArray assistantCoaches = json.getJSONArray("assistantCoach");
			if (assistantCoaches!=null&&assistantCoaches.size()!=0) {
				for (int i = 0; i < assistantCoaches.size(); i++) {
					JSONObject assistantCoach = assistantCoaches.getJSONObject(i);
					if (assistantCoach!=null&&assistantCoach.containsKey("id")) {
						ids.add("coach#"+assistantCoach.getString("id"));
					}
				}
			}
			JSONObject teamLeader = json.getJSONObject("teamLeader");
			if (teamLeader!=null &&teamLeader.containsKey("id")) {
				ids.add("leader#"+teamLeader.getString("id"));
			}
			JSONArray players = json.getJSONArray("players");
			if (players!=null&&players.size()!=0) {
				for (int i = 0; i < players.size(); i++) {
					JSONObject player = players.getJSONObject(i);
					if (player!=null&&player.containsKey("id")) {
						ids.add("player#"+player.getString("id"));
					}
				}
			}
			return ids;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

//	public static void main(String[] args) {
//		String apigateway = "http://192.168.50.154:8000";
//		System.out.println(GroupUtil.getNationalCoachIds(apigateway));
//		System.out.println(GroupUtil.getNationalPlayerIds(apigateway));
//		System.out.println(GroupUtil.getNationalRefereeIds(apigateway));
//		System.out.println(GroupUtil.getNationalTeam(apigateway));
//	}

}
