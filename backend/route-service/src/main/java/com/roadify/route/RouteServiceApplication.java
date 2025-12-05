package com.roadify.route;

import com.roadify.route.infrastructure.http.OrsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // optional

@SpringBootApplication
// @EnableDiscoveryClient // If you want explicit discovery client annotation
@ConfigurationPropertiesScan
public class RouteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouteServiceApplication.class, args);
    }
}
