package com.roadify.places.infrastructure.geoapify;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for Geoapify WebClient and properties binding.
 */
@Configuration
@EnableConfigurationProperties(GeoapifyProperties.class)
public class GeoapifyConfig {

    /**
     * WebClient used to call Geoapify Places API.
     */
    @Bean
    @Qualifier("geoapifyWebClient")
    public WebClient geoapifyWebClient(WebClient.Builder builder,
                                       GeoapifyProperties properties) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
