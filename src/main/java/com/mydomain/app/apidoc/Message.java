package com.mydomain.app.apidoc;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "消息", description = "消息的描述")
public class Message {

	@ApiModelProperty(value = "编号", example = "4cafc912-c35e-408c-8380-26d27a991981")
	private String id;
	
	@ApiModelProperty(value = "时间", example = "2019-01-24T01:34:43.771Z")
	private Date time;
	
	@ApiModelProperty("发送者")
	private String sender;
	
	@ApiModelProperty("接收者")
	private String receiver;
	
	@ApiModelProperty("标题")
	private String title;
	
	@ApiModelProperty("内容")
	private String body;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
}
