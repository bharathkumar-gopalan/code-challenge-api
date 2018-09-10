package com.hellofresh.qa.automation.api.model;

import java.util.List;

/**
 * A Model for the all country ISO2code message Please note that the variables
 * have been named to match the API response(and not CamelCasing) . Else Object
 * conversion from Json will fail
 * 
 * @author bharath
 *
 */
public class AllCountryCodeMessage {
	private List<String> messages;
	private List<CountryAlphaCodeMessage> result;

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public List<CountryAlphaCodeMessage> getResult() {
		return result;
	}

	public void setResult(List<CountryAlphaCodeMessage> result) {
		this.result = result;
	}

	

}
