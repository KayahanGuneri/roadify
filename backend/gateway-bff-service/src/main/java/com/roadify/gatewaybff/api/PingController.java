package com.roadify.gatewaybff.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PingController
 *
 * English:
 * Simple health check endpoint to verify that the gateway is running.
 *
 * Türkçe Özet:
 * Gateway'in ayakta olduğunu hızlıca kontrol etmek için kullanılan basit endpoint.
 */
@RestController
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("OK");
    }
}
