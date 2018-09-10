package com.hellofresh.qa.automation.api.model;

import java.util.List;

public class CountryCodeMessage {
	private List<String> messages;
	private CountryAlphaCodeMessage result;
	
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public CountryAlphaCodeMessage getResult() {
		return result;
	}
	public void setResult(CountryAlphaCodeMessage result) {
		this.result = result;
	}
	
	
	

}
