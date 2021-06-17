package com.serverless.handler.product;

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
import com.serverless.dynamo.entities.Product;

public class CreateProductHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'body' from input

			// create the Product object for post
			Product product = new ObjectMapper().readValue((String) input.get("body"), Product.class);
			//product.setId(generateProductId());
			product.save(product);

			// send the response back
			return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(product)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();

		} catch (Exception ex) {
			logger.error("Error in saving product: " + ex);

			// send the error response back
			Response responseBody = new Response("Error in saving product: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}

	private String generateProductId() {

		Random random = new Random();
		String number = String.format("%04d", random.nextInt(10000));
		return ("ITEM" + number);
	}
}