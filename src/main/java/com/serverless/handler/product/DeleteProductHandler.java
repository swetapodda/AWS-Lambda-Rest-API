package com.serverless.handler.product;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dynamo.entities.Product;

public class DeleteProductHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'pathParameters' from input
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String productId = pathParameters.get("id");

			// get the Product by id
			Boolean success = new Product().delete(productId);

			// send the response back
			if (success) {
				return ApiGatewayResponse.builder().setStatusCode(204)
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
			} else {
				return ApiGatewayResponse.builder().setStatusCode(404)
						.setObjectBody("Product with id: '" + productId + "' not found.")
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
			}
		} catch (Exception ex) {
			logger.error("Error in deleting product: " + ex);

			// send the error response back
			Response responseBody = new Response("Error in deleting product: ", input);
			return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless")).build();
		}
	}
}