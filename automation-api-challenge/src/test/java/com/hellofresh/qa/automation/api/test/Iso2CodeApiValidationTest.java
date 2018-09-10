package com.hellofresh.qa.automation.api.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.google.gson.Gson;
import com.hellofresh.qa.automation.api.Endpoint;
import com.hellofresh.qa.automation.api.messages.AddCountryRequestMessage;
import com.hellofresh.qa.automation.api.messages.AllCountryCodeApiResponseMessage;
import com.hellofresh.qa.automation.api.messages.CountryCodeApiResponseMessage;
import com.hellofresh.qa.automation.api.model.CountryAlphaCodeMessage;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Iso2CodeApiValidationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(Iso2CodeApiValidationTest.class);
	private final OkHttpClient client;
	private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
	private static final int API_OK_CODE = 200;
	private static final int API_CREATED_CODE = 201;

	// Expected error messages
	private static final String NO_COUNTRY_FOUND_MESSAGE = "No matching country found for requested code ";
	
	// Exception messages
	private static final String GET_REQUEST_ERROR_MESSAGE = "An error occurred while executing the GET request at endpoint %s , The message is %s";
	private static final String POST_REQUEST_ERROR_MESSAGE = "An error occurred while executing the POST request at endpoint %s , The message is %s";
	private static final String STRING_CONVERSION_ERROR_MESSAGE = "An error occurred while getting the JSON payload of the response body %s";

	// Assertion messages
	private static final String NON_SUCCESS_API_ASSERTION_MESSAGE = "Api Status code mismatch (%s Call endpoint %s)--";
	private static final String COUNTRY_NOT_IN_RESPONSE_ASSERTION_MESSAGE = "The country %s is in the response";
	private static final String INVALID_COUNTRY_ASSERTION_MESSAGE = "On giving an invalid country , The API response matches expected value --";
	

	/**
	 * Execute a get request
	 * 
	 * @param endpointUrl
	 *            The endpoint to execute the request at
	 * @return A Response object , containing the status of the call
	 * 
	 */
	private Response doGet(final String endpointUrl) {
		LOGGER.debug("Doing a get at the Url {}", endpointUrl);
		final Request request = new Request.Builder().url(endpointUrl).build();
		LOGGER.debug("executing the GET request ");
		try {
			return this.client.newCall(request).execute();
		} catch (Exception e) {
			throw new RuntimeException(String.format(GET_REQUEST_ERROR_MESSAGE, endpointUrl, e.getMessage()));
		}

	}
	
	private Response doPost(final String endpointUrl, final String payload){
		LOGGER.debug("Posting to the URL {} with payload {}", endpointUrl, payload);
		RequestBody payloadBody = RequestBody.create(JSON_TYPE, payload);
		final Request request = new Request.Builder().url(endpointUrl).post(payloadBody).build();
		try {
			return this.client.newCall(request).execute();
		} catch (Exception e) {
			throw new RuntimeException(String.format(POST_REQUEST_ERROR_MESSAGE, endpointUrl, e.getMessage()));
		}
	}

	/**
	 * Initialize the client at once during the test startup. This should be ok
	 * , The client is threadsafe and can be shared across multiple test cases
	 */
	public Iso2CodeApiValidationTest() {
		LOGGER.debug("Initializing the Client ...");
		this.client = new OkHttpClient();
	}

	/**
	 * Check to see if the given country code is in the response . NOTE ; This
	 * just validates to see if the alpha2_code is present in the response , It
	 * is possible to validate all the parame
	 * 
	 * @param responseMessage
	 *            The API response message
	 * @param countryCode
	 *            The country code to look for
	 * @return boolean value indicating if the country code is present or not
	 */
	private boolean isCountryInAllResponse(final AllCountryCodeApiResponseMessage responseMessage,
			final String countryCode) {
		LOGGER.debug("Checking to see if the country{} is in response ", countryCode);
		boolean isCountryInResponse = false;
		for (CountryAlphaCodeMessage message : responseMessage.getRestResponse().getResult()) {
			if (message.getAlpha2_code().equals(countryCode)) {
				LOGGER.debug("Located the country {} in the response code", countryCode);
				isCountryInResponse = true;
				break;
			}
		}
		return isCountryInResponse;
	}
	
	
	

	private boolean isCountryInResponse(final CountryCodeApiResponseMessage responseMessage,
			final String countryCode) {
		return responseMessage.getRestResponse().getResult().getAlpha2_code().equals(countryCode) ? true : false;
	}

	private <T> T convertJsonToResponseObject(final Response response, Class<T> responseClass) {
		String responseBody;
		try {
			responseBody = response.body().string();
			LOGGER.debug("The response is {}", responseBody);
		} catch (Exception e) {
			throw new RuntimeException(String.format(STRING_CONVERSION_ERROR_MESSAGE, e.getMessage()));
		}

		return new Gson().fromJson(responseBody, responseClass);
	}

	/**
	 * Scenario 1 : Get all countries and validate that US, DE and GB were
	 * returned in the response
	 * 
	 */

	@Test(description = "Check if the given countries are in response ", dataProvider = "allCountryDataProvider")
	public void testGetAllApiCall(String... countryArray) {
		LOGGER.debug("Testing to see if the countries UK , Germany and US are in response");
		final Response response = this.doGet(Endpoint.ALL_ISO2CODE_ENDPOINT);
		final int responseCode = response.code();
		LOGGER.debug("The api issued a response of {}", responseCode);
		// Check if the API returns a 200
		Assert.assertEquals(responseCode, API_OK_CODE,
				String.format(NON_SUCCESS_API_ASSERTION_MESSAGE, "GET", Endpoint.ALL_ISO2CODE_ENDPOINT.toString()));
		// Convert the json response to object
		final AllCountryCodeApiResponseMessage apiResponseMessage = (AllCountryCodeApiResponseMessage) convertJsonToResponseObject(
				response, AllCountryCodeApiResponseMessage.class);
		final SoftAssert softAssert = new SoftAssert();
		for (String countryCode : countryArray) {
			softAssert.assertTrue(this.isCountryInAllResponse(apiResponseMessage, countryCode),
					String.format(COUNTRY_NOT_IN_RESPONSE_ASSERTION_MESSAGE, countryCode));
		}
		softAssert.assertAll();
	}

	@DataProvider(name = "allCountryDataProvider")
	public Object[][] allCountryDataProvider() {
		return new String[][] { { "US", "GB", "DE" } };
	}

	/**
	 * Scenario2 : Get each country (US, DE and GB) individually and validate
	 * the response
	 * 
	 */

	@Test(description = "Check if a given countries is present in the response ", dataProvider = "countryDataProvider")
	public void testGetCountryApiCall(String countryCode) {
		LOGGER.debug("Testing to see if the country {} is available individually in api response", countryCode);
		final String endpoint = String.format(Endpoint.COUNTRY_ISO2CODE_ENDPOINT, countryCode);
		final Response response = this.doGet(endpoint);
		final int responseCode = response.code();
		LOGGER.debug("The api issued a response of {}", responseCode);
		// Check if the API returns a 200
		Assert.assertEquals(responseCode, API_OK_CODE,
				String.format(NON_SUCCESS_API_ASSERTION_MESSAGE, "GET", endpoint));
		final CountryCodeApiResponseMessage apiResponseMessage = this.convertJsonToResponseObject(response,
				CountryCodeApiResponseMessage.class);
		Assert.assertTrue(this.isCountryInResponse(apiResponseMessage, countryCode),
				String.format(COUNTRY_NOT_IN_RESPONSE_ASSERTION_MESSAGE, countryCode));
	}

	@DataProvider(name = "countryDataProvider")
	public Object[][] countryDataProvider() {
		return new String[][] { { "US" }, { "GB" }, { "DE" } };
	}
	
	
	
	/**
	 * Scenario 3 , Same as above but with AssertFalse, Code duplication , need to be fixed  
	 *
	 */
	
	
	@Test(description = "Check the response for an invalid country", dataProvider = "invalidCountryDataProvider")
	public void testGetInvalidCountryApiCall(String countryCode) {
		LOGGER.debug("Testing to see if the country {} is available individually in api response", countryCode);
		final String endpoint = String.format(Endpoint.COUNTRY_ISO2CODE_ENDPOINT, countryCode);
		final Response response = this.doGet(endpoint);
		final int responseCode = response.code();
		LOGGER.debug("The api issued a response of {}", responseCode);
		// Check if the API returns a 200
		Assert.assertEquals(responseCode, API_OK_CODE,
				String.format(NON_SUCCESS_API_ASSERTION_MESSAGE, "GET", Endpoint.COUNTRY_ISO2CODE_ENDPOINT.toString()));
		final CountryCodeApiResponseMessage apiResponseMessage = this.convertJsonToResponseObject(response,
				CountryCodeApiResponseMessage.class);
		final String apiMessage  = apiResponseMessage.getRestResponse().getMessages().get(0);
		Assert.assertTrue(apiMessage.contains(NO_COUNTRY_FOUND_MESSAGE), INVALID_COUNTRY_ASSERTION_MESSAGE);
	}

	@DataProvider(name = "invalidCountryDataProvider")
	public Object[][] invalidCountryDataProvider() {
		return new String[][] { { "FOO_BAR" } };
	}
	
	
	/**
	 * Build a add country request message 
	 * 
	 */
	private AddCountryRequestMessage buildRequest(){
		AddCountryRequestMessage message = new AddCountryRequestMessage();
		message.setName("Test Country");
		message.setAlpha2_code("TC");
		message.setAlpha3_code("TCY");
		return message;
	}
	
	/**
	 * scenario 4 : Write a test that would validate new country addition using POST(it will not work now, but no worries).
	 */
	@Test(description = "test if the user is able to add a new country, This will fail ")
	public void testCountryCodeAddition(){
		final AddCountryRequestMessage requestMessage = this.buildRequest();
		final String endpoint = String.format(Endpoint.COUNTRY_ISO2CODE_ENDPOINT, "");
		final Response response = this.doPost(endpoint, new Gson().toJson(requestMessage));
		final int responseCode = response.code();
		LOGGER.debug("The api issued a response of {}", responseCode);
		// Check if the API returns a 200
		Assert.assertEquals(responseCode, API_CREATED_CODE,
				String.format(NON_SUCCESS_API_ASSERTION_MESSAGE, "POST", endpoint));
		
	}
	
	

}
