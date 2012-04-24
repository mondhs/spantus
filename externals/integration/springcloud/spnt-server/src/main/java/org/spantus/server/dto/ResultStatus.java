package org.spantus.server.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ResultStatus")
public class ResultStatus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2284887033768349665L;
	private Boolean success;
	private List<String> messages;
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	public static ResultStatus success(){
		ResultStatus resultStatus = new ResultStatus();
		resultStatus.setSuccess(true);
		return resultStatus;
	}
	public static ResultStatus success(String... messages){
		ResultStatus status =success();
		status.setMessages(new ArrayList<String>());
		for (String msg : messages) {
			status.getMessages().add(msg);
		}
		return status;
	}
}
