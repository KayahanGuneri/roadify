package com.roadify.gatewaybff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GatewayBffApplication
 *
 * English:
 * API gateway / BFF for the Roadify mobile app.
 *
 * Türkçe Özet:
 * Roadify mobil uygulaması için istekleri mikroservislere yönlendiren
 * API gateway / BFF servisinin giriş noktası.
 */
@SpringBootApplication
public class GatewayBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayBffApplication.class, args);
    }
}
