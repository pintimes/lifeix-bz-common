package com.lifeix.bz.common.module.message.po;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.lifeix.football.common.model.Label;
import com.lifeix.football.common.model.OperAction;
import com.lifeix.football.common.model.User;

@Document(collection = "msgs")
public class MsgPO {

	@Id
	private String id;

	@Indexed
	private String senderId;

	@Field
	private User sender;

	@Field
	private User receiver;
	@Indexed
	private String receiverId;

	@Indexed
	private String app;

	@Indexed
	private boolean read;

	@Indexed
	private String categoryId;

	@Indexed
	private String type;

	@Field
	private List<Label> labels;

	private String title;

	private List<String> images;

	private String content;

	// 参数，json类型的
	@Field
	private OperAction action;

	@Indexed
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getImages() {
		return images;
	}

	public OperAction getAction() {
		return action;
	}

	public void setAction(OperAction action) {
		this.action = action;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

}
