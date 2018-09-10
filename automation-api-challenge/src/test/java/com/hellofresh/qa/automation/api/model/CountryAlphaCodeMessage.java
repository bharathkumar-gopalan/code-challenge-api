package com.hellofresh.qa.automation.api.model;

/**
 * A class representing the country ISO2 code message
 * 
 * Please note that the variables have been named to match the API response(and
 * not CamelCasing) . Else Object conversion from Json will fail
 * 
 * @author bharath
 *
 */
public class CountryAlphaCodeMessage {
	private String name;
	private String alpha2_code;
	private String alpha3_code;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlpha2_code() {
		return alpha2_code;
	}
	public void setAlpha2_code(String alpha2_code) {
		this.alpha2_code = alpha2_code;
	}
	public String getAlpha3_code() {
		return alpha3_code;
	}
	public void setAlpha3_code(String alpha3_code) {
		this.alpha3_code = alpha3_code;
	}
	
	

}
