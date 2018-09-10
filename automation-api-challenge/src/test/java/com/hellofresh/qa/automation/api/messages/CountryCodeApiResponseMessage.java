package com.hellofresh.qa.automation.api.messages;

import com.hellofresh.qa.automation.api.model.CountryCodeMessage;


/**
 * A model for the country specific  API response message.
 * 
 * Please note that the variables have been
 * named to match the API response(and not CamelCasing) . Else Object conversion
 * from Json will fail
 * 
 * @author bharath
 *
 */
public class CountryCodeApiResponseMessage {
	private CountryCodeMessage RestResponse;

	public CountryCodeMessage getRestResponse() {
		return RestResponse;
	}

	public void setRestResponse(CountryCodeMessage restResponse) {
		RestResponse = restResponse;
	}

	
	
	
	
	

}
