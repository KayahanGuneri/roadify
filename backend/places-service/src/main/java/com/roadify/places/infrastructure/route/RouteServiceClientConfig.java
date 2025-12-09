package com.roadify.places.infrastructure.route;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for RouteService WebClient.
 */
@Configuration
@EnableConfigurationProperties(RouteServiceProperties.class)
public class RouteServiceClientConfig {

    @Bean
    public WebClient routeServiceWebClient(RouteServiceProperties properties, WebClient.Builder builder) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
