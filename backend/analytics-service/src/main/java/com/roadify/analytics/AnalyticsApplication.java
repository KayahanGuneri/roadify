package com.roadify.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Analytics Service.
 *
 * Turkish Summary:
 * Analytics-service Spring Boot giriş noktasıdır.
 * Kafka event'lerini consume eder ve JDBC ile aggregate tablolarını günceller.
 */
@SpringBootApplication
public class AnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
    }
}
