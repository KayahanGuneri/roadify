package com.roadify.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ConfigServerApplication
 *
 * English:
 * Centralized configuration server for all Roadify microservices.
 *
 * Türkçe Özet:
 * Tüm Roadify mikroservisleri için merkezi konfigürasyon sunan service.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
