package com.roadify.aiassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 * Entry point for the AI Assistant Service.
 * This service will talk to the self-hosted LLM and compose AI recommendations.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AiAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAssistantApplication.class, args);
    }
}
