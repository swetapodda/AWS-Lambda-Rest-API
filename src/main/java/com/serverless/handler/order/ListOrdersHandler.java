package com.serverless.handler.order;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dynamo.entities.Order;

public class ListOrdersHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {
			// get all orders
			List<Order> orders = new Order().list();

			// send the response back
			return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(orders)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		} catch (Exception ex) {
			logger.error("Error in listing orders: " + ex);

			// send the error response back
			Response responseBody = new Response("Error in listing orders: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}
}