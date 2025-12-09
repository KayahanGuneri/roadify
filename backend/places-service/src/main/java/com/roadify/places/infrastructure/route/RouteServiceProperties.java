package com.roadify.places.infrastructure.route;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for route-service client.
 */
@Data
@ConfigurationProperties(prefix = "roadify.route-service")
public class RouteServiceProperties {
    private String baseUrl;
}
