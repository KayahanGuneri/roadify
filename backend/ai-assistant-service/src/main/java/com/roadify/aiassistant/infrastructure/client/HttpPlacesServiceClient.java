package com.roadify.aiassistant.infrastructure.client;

import com.roadify.aiassistant.application.client.PlacesServiceClient;
import com.roadify.aiassistant.application.dto.PlaceDetailsDTO;
import com.roadify.aiassistant.api.dto.AISuggestionFiltersDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class HttpPlacesServiceClient implements PlacesServiceClient {

    private final RestTemplate restTemplate;
    private final HttpServletRequest httpServletRequest;
    private final String gatewayBaseUrl;

    public HttpPlacesServiceClient(
            RestTemplate restTemplate,
            HttpServletRequest httpServletRequest,
            @Value("${roadify.services.gateway-base-url}") String gatewayBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.httpServletRequest = httpServletRequest;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public List<PlaceDetailsDTO> getPlacesForRoute(String routeId, AISuggestionFiltersDTO filters) {

        HttpHeaders headers = new HttpHeaders();

        // JWT forward
        String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }

        // X-User-Id forward
        String userIdHeader = httpServletRequest.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            headers.set("X-User-Id", userIdHeader);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Build URL
        StringBuilder urlBuilder = new StringBuilder(
                gatewayBaseUrl + "/api/routes/" + routeId + "/places"
        );

        boolean first = true;
        if (filters != null) {

            if (filters.getCategory() != null && !filters.getCategory().isBlank()) {
                urlBuilder.append(first ? "?" : "&");
                urlBuilder.append("category=")
                        .append(URLEncoder.encode(filters.getCategory(), StandardCharsets.UTF_8));
                first = false;
            }

            if (filters.getMaxDetourKm() != null) {
                urlBuilder.append(first ? "?" : "&");
                urlBuilder.append("maxDetourKm=").append(filters.getMaxDetourKm());
                first = false;
            }

            if (filters.getLimit() != null) {
                urlBuilder.append(first ? "?" : "&");
                urlBuilder.append("limit=").append(filters.getLimit());
                first = false;
            }

            if (filters.getOffset() != null) {
                urlBuilder.append(first ? "?" : "&");
                urlBuilder.append("offset=").append(filters.getOffset());
            }
        }

        String url = urlBuilder.toString();

        ResponseEntity<PlaceDetailsDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PlaceDetailsDTO[].class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException(
                    "Failed to fetch places from gateway. Status: " + response.getStatusCode()
            );
        }

        return List.of(response.getBody());
    }
}
