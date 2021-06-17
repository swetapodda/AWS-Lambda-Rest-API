package com.serverless.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class BodyTypeConverter implements DynamoDBTypeConverter<Object, Object> {

	@Override
	public Object convert(Object object) {
		return (String) object;
	}

	@Override
	public Object unconvert(Object object) {
		return (String) object;
	}
}
