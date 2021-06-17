package com.serverless.dynamo.entities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.serverless.dynamo.DynamoDBAdapter;

@DynamoDBTable(tableName = "PLACEHOLDER_PRODUCT_TABLE_NAME")
public class Product {

	private static final String PRODUCT_TABLE_NAME = System.getenv("PRODUCT_TABLE");
	private final Log logger = LogFactory.getLog(this.getClass());

	private static DynamoDBAdapter db_adapter;
	private final AmazonDynamoDB client;
	private final DynamoDBMapper mapper;

	@DynamoDBRangeKey(attributeName = "id")
	private String id;
	@DynamoDBHashKey(attributeName = "orderId")
	private String orderId;
	@DynamoDBAttribute(attributeName = "name")
	private String name;
	@DynamoDBAttribute(attributeName = "price")
	private Float price;

	@SuppressWarnings("static-access")
	public Product() {
		DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
				.withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(PRODUCT_TABLE_NAME)).build();
		this.db_adapter = DynamoDBAdapter.getInstance();
		this.client = this.db_adapter.getDbClient();
		this.mapper = this.db_adapter.createDbMapper(mapperConfig);
	}

	// methods
	public Boolean ifTableExists() {
		return this.client.describeTable(PRODUCT_TABLE_NAME).getTable().getTableStatus().equals("ACTIVE");
	}

	public List<Product> list() throws IOException {
		DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
		List<Product> results = this.mapper.scan(Product.class, scanExp);
		for (Product p : results) {
			logger.info("Products - list(): " + p.toString());
		}
		return results;
	}

	public Product get(String id) throws IOException {
		Product product = null;

		HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
		av.put(":v1", new AttributeValue().withS(id));

		DynamoDBQueryExpression<Product> queryExp = new DynamoDBQueryExpression<Product>()
				.withKeyConditionExpression("id = :v1").withExpressionAttributeValues(av);

		PaginatedQueryList<Product> result = this.mapper.query(Product.class, queryExp);
		if (result.size() > 0) {
			product = result.get(0);
			logger.info("Products - get(): product - " + product.toString());
		} else {
			logger.info("Products - get(): product - Not Found.");
		}
		return product;
	}

	public void save(Product product) throws IOException {
		logger.info("Products - save(): " + product.toString());
		this.mapper.save(product);
	}

	public Boolean delete(String id) throws IOException {
		Product product = null;

		// get product if exists
		product = get(id);
		if (product != null) {
			logger.info("Products - delete(): " + product.toString());
			this.mapper.delete(product);
		} else {
			logger.info("Products - delete(): product - does not exist.");
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return String.format("Product [id=%s, orderId=%s, name=%s, price=%s]", id, orderId, name, price);
	}

}