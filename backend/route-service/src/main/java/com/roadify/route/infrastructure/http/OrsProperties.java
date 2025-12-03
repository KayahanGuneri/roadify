package com.roadify.route.infrastructure.http;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for OpenRouteService.
 */
@Component
@ConfigurationProperties(prefix = "ors")
public class OrsProperties {

    /**
     * Base URL of the ORS API, e.g. https://api.openrouteservice.org
     */
    private String baseUrl;

    /**
     * API key for ORS.
     */
    private String apiKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
