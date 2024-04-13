package com.write.api.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.write.api.aws.DynamoDBService;
import com.write.api.utils.Constants;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
public class URLRepository {

    @Autowired
    private DynamoDBService dynamoDBService;

    public void insert(String shortURL, String longURL) throws Exception {
        String shard = getCorrespondingShard(shortURL);
        Map<String, AttributeValue> item = buildItem(shortURL, longURL);
        dynamoDBService.insert(item, shard);
    }

    public void delete(String shortURL) {
        String shard = getCorrespondingShard(shortURL);
        dynamoDBService.deleteItem(shortURL, shard);
    }

    public Boolean checkIfShortURLExists(String shortURL) {
        String shard = getCorrespondingShard(shortURL);
        Map<String, AttributeValue> item = dynamoDBService.getItem(shortURL, shard);

        return item.get("short_url") != null;
    }

    private Map<String, AttributeValue> buildItem(String shortURL, String longURL) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("short_url", AttributeValue.builder().s(shortURL).build());
        item.put("long_url", AttributeValue.builder().s(longURL).build());
        item.put("created_at", AttributeValue.builder().s(String.valueOf(System.currentTimeMillis())).build());
        return item;
    }

    private String getCorrespondingShard(String shortURL) {
        char firstChar = shortURL.charAt(0);

        if (firstChar >= '0' && firstChar <= '9') {
            return Constants.DB_SHARD_A;
        } else if (firstChar >= 'a' && firstChar <= 'm') {
            return Constants.DB_SHARD_B;
        } else if (firstChar >= 'n' && firstChar <= 'z') {
            return Constants.DB_SHARD_C;
        } else if (firstChar >= 'A' && firstChar <= 'M') {
            return Constants.DB_SHARD_D;
        } else if (firstChar >= 'N' && firstChar <= 'Z') {
            return Constants.DB_SHARD_E;
        }

        throw new IllegalArgumentException("Invalid short URL");
    }
	
}
