package com.roadify.places.infrastructure.overpass;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "roadify.overpass")
public class OverpassProperties {
    private String baseUrl;
}
