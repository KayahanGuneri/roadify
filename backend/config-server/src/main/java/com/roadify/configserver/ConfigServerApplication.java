package com.roadify.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
// @EnableConfigServer   // <-- ŞİMDİLİK KALDIR / YORUM SATIRINA AL
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
