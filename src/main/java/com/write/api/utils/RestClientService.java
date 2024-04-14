package com.write.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.exception.BadRequestException;
import com.write.api.exception.ForbiddenException;
import com.write.api.exception.NotFoundException;

import org.apache.hc.client5.http.ClientProtocolException;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RestClientService {

    protected static final ObjectMapper mapper = new ObjectMapper();

    protected Boolean success(int status) {
        return status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION;
    }

    protected PoolingHttpClientConnectionManager getConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        return connectionManager;
    }

    protected <T> T executeRequestInternal(HttpUriRequestBase request, Class<T> responseType, HttpClientResponseHandler<T> responseHandler) throws Exception {
        PoolingHttpClientConnectionManager connectionManager = getConnectionManager();

        try (CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()) {

            T response = client.execute(request, responseHandler);
            client.close();
            return response;

        } catch (Exception e) {
            throw e;
        }
    }

    public <T> T executeRequest(HttpUriRequestBase request, Class<T> responseType) throws Exception {
        HttpClientResponseHandler<T> responseHandler = new HttpClientResponseHandler<T>() {
            @Override
            public T handleResponse(final ClassicHttpResponse response) throws IOException, ParseException {
                final int status = response.getCode();
                final HttpEntity entity = response.getEntity();
                if (success(status)) {
                    return mapper.readValue(entity.getContent(), responseType);
                } else {
                    try {
                        handleResponseStatus(status, entity);
                    } catch (ParseException | IOException | BadRequestException e) {
                        e.printStackTrace();
                    }
                }
                return null; // esta linea se ejecuta solo en el caso de un 404 manejado por la api.
            }
        };

        return executeRequestInternal(request, responseType, responseHandler);
    }

    protected void handleResponseStatus(int status, HttpEntity entity) throws IOException, ParseException, BadRequestException {
        String message = EntityUtils.toString(entity);

        if (status == HttpStatus.SC_NOT_FOUND) {
            if (isCommonNotFoundException(message)) {
                throw new NotFoundException("Resource not found. Exception message: " + message);
            } else {
                return;
            }
        } else if (status == HttpStatus.SC_BAD_REQUEST) {
            throw new BadRequestException("Bad request. Exception message: " + message);
        } else if (status == HttpStatus.SC_FORBIDDEN) {
            throw new ForbiddenException("Forbidden. Exception message: " + message);
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status + ". Exception message: " + message);
        }
    }

    private Boolean isCommonNotFoundException(String message) {
        return message.contains("No message available");
    }
}
