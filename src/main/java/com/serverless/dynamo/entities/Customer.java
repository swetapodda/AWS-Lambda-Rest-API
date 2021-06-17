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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.serverless.dynamo.DynamoDBAdapter;

@DynamoDBTable(tableName = "PLACEHOLDER_CUSTOMER_TABLE_NAME")
public class Customer {

	private static final String CUSTOMER_TABLE = System.getenv("CUSTOMER_TABLE");
	private final Log logger = LogFactory.getLog(this.getClass());

	private static DynamoDBAdapter db_adapter;
	private final AmazonDynamoDB client;
	private final DynamoDBMapper mapper;

	@DynamoDBHashKey(attributeName = "id")
	private String id;
	@DynamoDBAttribute(attributeName = "name")
	private String name;
	@DynamoDBAttribute(attributeName = "address")
	private String address;

	@DynamoDBTypeConvertedJson
	private List<Order> orders;

	@SuppressWarnings("static-access")
	public Customer() {
		DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
				.withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(CUSTOMER_TABLE)).build();
		this.db_adapter = DynamoDBAdapter.getInstance();
		this.client = this.db_adapter.getDbClient();
		this.mapper = this.db_adapter.createDbMapper(mapperConfig);
	}

	// methods
	public Boolean ifTableExists() {
		return this.client.describeTable(CUSTOMER_TABLE).getTable().getTableStatus().equals("ACTIVE");
	}

	public List<Customer> list() throws IOException {
		Order order = new Order();
		DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
		List<Customer> results = this.mapper.scan(Customer.class, scanExp);
		for (Customer c : results) {
			c.setOrders(order.getAllByCustomerId(c.getId()));
		}
		logger.info("Customers - list(): " + results.toString());
		return results;
	}

	public Customer get(String customerId) throws IOException {
		Order order = new Order();
		Customer customer = null;

		HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
		av.put(":v1", new AttributeValue().withS(customerId));

		DynamoDBQueryExpression<Customer> queryExp = new DynamoDBQueryExpression<Customer>()
				.withKeyConditionExpression("id = :v1").withExpressionAttributeValues(av);

		PaginatedQueryList<Customer> result = this.mapper.query(Customer.class, queryExp);
		if (result.size() > 0) {
			customer = result.get(0);
			customer.setOrders(order.getAllByCustomerId(customer.getId()));
			logger.info("Customers - get(): customer - " + customer.toString());
		} else {
			logger.info("Customers - get(): customer - Not Found.");
		}
		return customer;
	}

	public void save(Customer customer) throws IOException {
		logger.info("Customers - save(): " + customer.toString());
		this.mapper.save(customer);
	}

	public Boolean delete(String id) throws IOException {
		Customer customer = null;

		// Check if customer exists
		customer = get(id);
		if (customer != null) {
			logger.info("Customers - delete(): " + customer.toString());
			this.mapper.delete(customer);
		} else {
			logger.info("Customers - delete(): customer - does not exist.");
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return String.format("Customer [id=%s, name=%s, address=%s, orders=%s]", id, name, address, orders);
	}

}