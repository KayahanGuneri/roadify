package com.roadify.aiassistant.infrastructure.client;

import com.roadify.aiassistant.api.dto.AISuggestionFiltersDTO;
import com.roadify.aiassistant.application.client.PlacesServiceClient;
import com.roadify.aiassistant.application.dto.PlaceDetailsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * HTTP implementation of PlacesServiceClient using gateway-bff-service.
 */
@Component
public class HttpPlacesServiceClient implements PlacesServiceClient {

    private final RestTemplate restTemplate;
    private final String gatewayBaseUrl;

    public HttpPlacesServiceClient(
            RestTemplate restTemplate,
            @Value("${roadify.gateway.base-url}") String gatewayBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public List<PlaceDetailsDTO> getPlacesForRoute(String routeId, AISuggestionFiltersDTO filters) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(gatewayBaseUrl + "/places/v1/routes/{routeId}/places");

        if (filters != null) {
            if (filters.getCategory() != null && !filters.getCategory().isBlank()) {
                builder.queryParam("category", filters.getCategory());
            }
            if (filters.getMaxDetourKm() != null) {
                builder.queryParam("maxDetourKm", filters.getMaxDetourKm());
            }
            if (filters.getLimit() != null) {
                builder.queryParam("limit", filters.getLimit());
            }
            if (filters.getOffset() != null) {
                builder.queryParam("offset", filters.getOffset());
            }
        }

        String url = builder.buildAndExpand(routeId).toUriString();

        ResponseEntity<PlaceDetailsDTO[]> response =
                restTemplate.getForEntity(url, PlaceDetailsDTO[].class);

        PlaceDetailsDTO[] body = response.getBody();
        if (body == null) {
            return List.of();
        }
        return Arrays.asList(body);
    }
}
