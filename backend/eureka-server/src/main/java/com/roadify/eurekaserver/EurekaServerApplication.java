package com.roadify.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * EurekaServerApplication
 *
 * English:
 * Discovery server where all backend services will register.
 *
 * Türkçe Özet:
 * Tüm backend servislerinin register olduğu discovery (Eureka) sunucusu.
 */
@SpringBootApplication
//@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
