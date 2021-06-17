package com.serverless.handler.order;

import java.util.Collections;
import java.util.Date;
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
import com.serverless.dynamo.entities.Order;

public class CreateOrderHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	@SuppressWarnings("unchecked")
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		logger.info("Inside Create Order handler: ");
		try {

			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String customerId = pathParameters.get("custId");
			Customer customer = new Customer().get(customerId);
			if (customer != null) {
				Order order = new ObjectMapper().readValue((String) input.get("body"), Order.class);
				logger.info("Order in saving order: " + order);
				order.setId(this.generateOrderId());
				order.setCustId(customerId);
				order.setOrderDate(new Date());
				logger.info("INFO in retrieving order: " + order);
				order.save(order);

				return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(order)
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
			} else
				return ApiGatewayResponse.builder().setStatusCode(404)
						.setObjectBody("Customer with id: '" + customerId + "' not found.")
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();

		} catch (Exception ex) {
			logger.error("Error in saving order: " + ex);
			Response responseBody = new Response("Error in saving order: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}

	private String generateOrderId() {

		Random random = new Random();
		String number = String.format("%04d", random.nextInt(10000));
		return ("ORDNO" + number);
	}
}