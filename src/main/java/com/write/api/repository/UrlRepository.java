package com.write.api.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.write.api.aws.DynamoDBService;
import com.write.api.exception.NotFoundException;
import com.write.api.utils.Constants;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
public class UrlRepository {

    @Autowired
    private DynamoDBService dynamoDBService;

    public void insert(String shortUrl, String longUrl) throws Exception {
        String shard = getCorrespondingShard(shortUrl);
        Map<String, AttributeValue> item = buildItem(shortUrl, longUrl);
        dynamoDBService.insert(item, shard);
    }

    public String getLongUrl(String shortUrl) {
        String shard = getCorrespondingShard(shortUrl);
        Map<String, AttributeValue> item = dynamoDBService.getItem(shortUrl, shard);

        if (item == null) {
            throw new NotFoundException("Short URL " + shortUrl + " not found");
        }

        return item.get("long_url").s();
    }

    public Boolean checkIfShortUrlExists(String shortUrl) {
        String shard = getCorrespondingShard(shortUrl);
        Map<String, AttributeValue> item = dynamoDBService.getItem(shortUrl, shard);

        return item.get("short_url") != null;
    }

    private Map<String, AttributeValue> buildItem(String shortUrl, String longUrl) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("short_url", AttributeValue.builder().s(shortUrl).build());
        item.put("long_url", AttributeValue.builder().s(longUrl).build());
        item.put("created_at", AttributeValue.builder().s(String.valueOf(System.currentTimeMillis())).build());
        return item;
    }

    private String getCorrespondingShard(String shortUrl) {
        char firstChar = shortUrl.charAt(0);

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
