package com.serverless.handler.order;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dynamo.entities.Customer;
import com.serverless.dynamo.entities.Order;

public class GetOrderHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	@SuppressWarnings("unchecked")
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'pathParameters' from input
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String custId = pathParameters.get("custId");
			Customer customer = new Customer().get(custId);
			if (customer != null) {
				String orderId = pathParameters.get("orderId");
				Order order = new Order().get(custId, orderId);
				if (order != null)
					return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(order)
							.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
				else
					return ApiGatewayResponse.builder().setStatusCode(404)
							.setObjectBody("Order with id: '" + orderId + "' not found.")
							.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
			} else
				return ApiGatewayResponse.builder().setStatusCode(404)
						.setObjectBody("Customer with id: '" + custId + "' not found.")
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		} catch (Exception ex) {
			logger.error("Error in retrieving order: " + ex);

			// send the error response back
			Response responseBody = new Response("Error in retrieving order: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}
}