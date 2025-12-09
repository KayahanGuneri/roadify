package com.roadify.places.infrastructure.overpass;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(OverpassProperties.class)
public class OverpassClientConfig {

    @Bean
    public WebClient overpassWebClient(OverpassProperties properties, WebClient.Builder builder) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
