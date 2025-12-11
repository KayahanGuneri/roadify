package com.roadify.places.infrastructure.geoapify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Geoapify Places API.
 * Binds roadify.geoapify.* from application.yml.
 */
@Data
@ConfigurationProperties(prefix = "roadify.geoapify")
public class GeoapifyProperties {

    /**
     * Base URL for Geoapify Places API.
     * Example: https://api.geoapify.com/v2/places
     */
    private String baseUrl;

    /**
     * API key for Geoapify.
     */
    private String apiKey;

    /**
     * Optional timeout in milliseconds (not yet used, but can be wired later).
     */
    private Integer timeoutMs;
}
