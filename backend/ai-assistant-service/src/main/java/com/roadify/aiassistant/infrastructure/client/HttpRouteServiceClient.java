package com.roadify.aiassistant.infrastructure.client;

import com.roadify.aiassistant.application.client.RouteServiceClient;
import com.roadify.aiassistant.application.dto.RouteDetailsDTO;
import com.roadify.aiassistant.domain.route.RouteServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class HttpRouteServiceClient implements RouteServiceClient {

    private final RestTemplate restTemplate;
    private final HttpServletRequest httpServletRequest;
    private final String gatewayBaseUrl;

    public HttpRouteServiceClient(
            RestTemplate restTemplate,
            HttpServletRequest httpServletRequest,
            @Value("${roadify.services.gateway-base-url}") String gatewayBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.httpServletRequest = httpServletRequest;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public RouteDetailsDTO getRouteById(String routeId) {
        String authHeader = httpServletRequest.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }

        String userIdHeader = httpServletRequest.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            headers.set("X-User-Id", userIdHeader);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = gatewayBaseUrl + "/api/routes/" + routeId;

        try {
            log.debug("Calling route-service via gateway. url={}", url);

            ResponseEntity<RouteDetailsDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RouteDetailsDTO.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("route-service returned non-2xx or empty body. status={}, url={}",
                        response.getStatusCode(), url);
                throw new RouteServiceException(
                        "Failed to fetch route details from gateway. Status: " + response.getStatusCode(),
                        routeId
                );
            }

            return response.getBody();

        } catch (RestClientException ex) {
            log.error("Error while calling route-service via gateway. url={}", url, ex);
            throw new RouteServiceException(
                    "I/O error while calling route-service via gateway",
                    routeId,
                    ex
            );
        } catch (RouteServiceException ex) {

            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while calling route-service via gateway. url={}", url, ex);
            throw new RouteServiceException(
                    "Unexpected error while calling route-service via gateway",
                    routeId,
                    ex
            );
        }
    }
}
