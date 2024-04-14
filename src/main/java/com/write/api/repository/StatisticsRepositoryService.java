package com.write.api.repository;

import java.nio.charset.StandardCharsets;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.write.api.dto.CrudStatusDto;
import com.write.api.request.URLPairRequest;
import com.write.api.utils.Constants;
import com.write.api.utils.RestClientService;

@Service
public class StatisticsRepositoryService {
    
    @Autowired
    private RestClientService restClientService;

    @Autowired
    private Environment env;

    public CrudStatusDto create(String shortURL, String longURL) throws Exception {

        String baseUrl = env.getProperty(Constants.STATISTICS_BASE_URL);
        HttpPost request = new HttpPost(baseUrl + "/internal/statistics/long_url");

        URLPairRequest body = URLPairRequest.builder().shortURL(shortURL).longURL(longURL).build();

        String URLPairRequest = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create().toJson(body);

        request.setEntity(new StringEntity(URLPairRequest, StandardCharsets.UTF_8));
        request.addHeader("Content-Type", "application/json");

        return restClientService.executeRequest(request, CrudStatusDto.class);
    }

    public CrudStatusDto delete(String shortURL) throws Exception {

        String baseUrl = env.getProperty(Constants.STATISTICS_BASE_URL);
        HttpDelete request = new HttpDelete(baseUrl + "/internal/statistics/short_url/" + shortURL);

        return restClientService.executeRequest(request, CrudStatusDto.class);
    }

}
