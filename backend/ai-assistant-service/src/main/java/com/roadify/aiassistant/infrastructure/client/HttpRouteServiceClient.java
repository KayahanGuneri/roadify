package com.roadify.aiassistant.infrastructure.client;

import com.roadify.aiassistant.application.client.RouteServiceClient;
import com.roadify.aiassistant.application.dto.RouteDetailsDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP implementation of RouteServiceClient.
 *
 * Gateway üzerinden route-service'e istek atar:
 *   GET {gatewayBaseUrl}/api/routes/{routeId}
 *
 * Mevcut request'teki Authorization ve X-User-Id header'larını forward eder.
 */
@Component
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
        // 1) Caller'dan gelen Authorization header'ını al
        String authHeader = httpServletRequest.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }

        // 2) X-User-Id'yi de forward et (gateway bunu kullanıyor)
        String userIdHeader = httpServletRequest.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            headers.set("X-User-Id", userIdHeader);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 3) Artık route-service'e direkt değil, GATEWAY üzerinden gidiyoruz:
        //    /routes/v1/... değil /api/routes/{id}
        String url = gatewayBaseUrl + "/api/routes/" + routeId;

        ResponseEntity<RouteDetailsDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                RouteDetailsDTO.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException(
                    "Failed to fetch route details from gateway. Status: " + response.getStatusCode()
            );
        }

        return response.getBody();
    }
}
