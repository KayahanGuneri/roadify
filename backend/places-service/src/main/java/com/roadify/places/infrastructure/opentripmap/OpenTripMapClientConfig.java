package com.roadify.places.infrastructure.opentripmap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(OpenTripMapProperties.class)
public class OpenTripMapClientConfig {

    @Bean
    public WebClient openTripMapWebClient(OpenTripMapProperties properties, WebClient.Builder builder) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Accept", "application/json")
                .defaultHeader("X-OTM-API-KEY", properties.getApiKey())
                .build();
    }
}
