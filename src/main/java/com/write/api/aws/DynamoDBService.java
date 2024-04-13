package com.write.api.aws;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.regions.Region;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DynamoDBService {

    private final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();

    public void insert(Map<String, AttributeValue> item, String shard) {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(shard)
                .item(item)
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }

    public Map<String, AttributeValue> getItem(String shortURL, String shard) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("short_url", AttributeValue.builder().s(shortURL).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(shard)
                .key(key)
                .build();

        GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        return getItemResponse.item();
    }

    public void deleteItem(String shortURL, String shard) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("short_url", AttributeValue.builder().s(shortURL).build());

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(shard)
                .key(key)
                .build();

        dynamoDbClient.deleteItem(deleteItemRequest);
    }

    public void cleanUp() {
        dynamoDbClient.close();
    }

}
