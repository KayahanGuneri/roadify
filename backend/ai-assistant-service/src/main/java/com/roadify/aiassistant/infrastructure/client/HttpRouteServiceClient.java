package com.roadify.aiassistant.infrastructure.client;

import com.roadify.aiassistant.application.client.RouteServiceClient;
import com.roadify.aiassistant.application.dto.RouteDetailsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP implementation of RouteServiceClient using gateway-bff-service.
 */
@Component
public class HttpRouteServiceClient implements RouteServiceClient {

    private final RestTemplate restTemplate;
    private final String gatewayBaseUrl;

    public HttpRouteServiceClient(
            RestTemplate restTemplate,
            @Value("${roadify.gateway.base-url}") String gatewayBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public RouteDetailsDTO getRouteById(String routeId) {
        String url = gatewayBaseUrl + "/routes/v1/routes/{id}";
        return restTemplate.getForObject(url, RouteDetailsDTO.class, routeId);
    }
}
