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

public class DeleteCustomerHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'pathParameters' from input
			@SuppressWarnings("unchecked")
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String custId = pathParameters.get("custId");

			// get the Customer by id
			Boolean success = new Customer().delete(custId);

			// send the response back
			if (success) {
				return ApiGatewayResponse.builder().setStatusCode(204)
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
			} else {
				return ApiGatewayResponse.builder().setStatusCode(404)
						.setObjectBody("Customer with id: '" + custId + "' not found.")
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
			}
		} catch (Exception ex) {
			logger.error("Error in deleting customer: " + ex);

			// send the error response back
			Response responseBody = new Response("Error in deleting customer: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}
}