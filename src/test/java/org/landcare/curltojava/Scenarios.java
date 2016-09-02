package org.landcare.curltojava;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import static org.junit.Assert.assertTrue;

public class Scenarios {

	private String curlCommand;

	@Before
	public final void setUp() {
	}

	/*
	@Given("^The curl command \"([^\"]*)\"$")
	public void The_curl_command(String arg1) throws Throwable {
		this.curlCommand = arg1;
	}
	*/

	@Given("^The curl command$")
	public void The_curl_command(String arg1) throws Throwable {
		this.curlCommand = arg1;
	}

	@Then("^The response should contain the property \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void The_response_should_contain_the_property_with_value(String arg1, String arg2) throws Throwable {
		CurlTransformer ct = new CurlTransformer(curlCommand);
		String key = arg1;
		String value = arg2;
		assertTrue("JSON key: " + key + " is value: " + value, ct.getJson().get(key).equals(value));
	}

	@Then("^The response's \"([^\"]*)\" property should contain the property \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void The_response_s_property_should_contain_the_property_with_value(String arg1, String arg2, String arg3) throws Throwable {
		CurlTransformer ct = new CurlTransformer(curlCommand);
		String property = arg1;
		String key = arg2;
		String value = arg3;
		JSONObject json = ct.getJson().getJSONObject(property);
		System.out.println("json: " + json.toString(2));
		assertTrue("JSON property: " + property + " key: " + key
				+ " is value: " + value, json.get(key).equals(value));
	}

	@After
	public final void tearDown() {
	}

}
