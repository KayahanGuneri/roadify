package com.roadify.places.infrastructure.opentripmap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "roadify.opentripmap")
public class OpenTripMapProperties {
    private String baseUrl;
    private String apiKey;
}
