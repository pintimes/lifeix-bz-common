package com.lifeix.bz.common.module.comment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "comment")
public class CommentConfig {

	private String userhost;
	private String userport;
	private String decisionhost;
	private String decisionport;

	public String getUserhost() {
		return userhost;
	}

	public void setUserhost(String userhost) {
		this.userhost = userhost;
	}

	public String getUserport() {
		return userport;
	}

	public void setUserport(String userport) {
		this.userport = userport;
	}

	public String getDecisionhost() {
		return decisionhost;
	}

	public void setDecisionhost(String decisionhost) {
		this.decisionhost = decisionhost;
	}

	public String getDecisionport() {
		return decisionport;
	}

	public void setDecisionport(String decisionport) {
		this.decisionport = decisionport;
	}

}