package com.serverless.handler.customer;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dynamo.entities.Customer;

public class CreateCustomerHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'body' from input

			Customer customer = new ObjectMapper().readValue((String) input.get("body"), Customer.class);
			customer.setId(generateCustomerId(customer.getName()));
			customer.save(customer);
			return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(customer)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();

		} catch (Exception ex) {
			logger.error("Error in saving customer: " + ex);
			Response responseBody = new Response("Error in saving customer: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}

	private String generateCustomerId(String name) {

		Random random = new Random();
		String number = String.format("%04d", random.nextInt(10000));
		return (name.substring(0, 2).toUpperCase() + number);
	}

}