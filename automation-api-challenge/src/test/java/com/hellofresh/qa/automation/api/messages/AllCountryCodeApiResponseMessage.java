package com.hellofresh.qa.automation.api.messages;

import com.hellofresh.qa.automation.api.model.AllCountryCodeMessage;

/**
 * A model for the All country API response message.
 * 
 * Please note that the variables have been
 * named to match the API response(and not CamelCasing) . Else Object conversion
 * from Json will fail
 * 
 * @author bharath
 *
 */
public class AllCountryCodeApiResponseMessage {
	private AllCountryCodeMessage RestResponse;

	public AllCountryCodeMessage getRestResponse() {
		return RestResponse;
	}

	public void setRestResponse(AllCountryCodeMessage restResponse) {
		RestResponse = restResponse;
	}

}
