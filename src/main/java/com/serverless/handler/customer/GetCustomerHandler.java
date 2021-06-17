package com.serverless.handler.customer;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dynamo.entities.Customer;

public class GetCustomerHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	@SuppressWarnings("unchecked")
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String custId = pathParameters.get("custId");
			Customer customer = new Customer().get(custId);
			if (customer != null)
				return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(customer)
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
			else
				return ApiGatewayResponse.builder().setStatusCode(404)
						.setObjectBody("Customer with id: '" + custId + "' not found.")
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		} catch (Exception ex) {
			logger.error("Error in retrieving customer: " + ex);
			Response responseBody = new Response("Error in retrieving customer: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}
}