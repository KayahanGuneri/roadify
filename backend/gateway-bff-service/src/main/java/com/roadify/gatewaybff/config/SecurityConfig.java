// backend/gateway-bff-service/src/main/java/com/roadify/gatewaybff/config/SecurityConfig.java
package com.roadify.gatewaybff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Gateway BFF security configuration.
 *
 * English:
 * - Validates JWT from Keycloak on incoming /api/** requests.
 * - Protects /api/mobile/** (mobile BFF endpoints) with "authenticated".
 *
 * Türkçe Özet:
 * - /api/** isteklerinde Keycloak JWT doğrulaması yapar.
 * - /api/mobile/** altındaki BFF endpointlerini authenticated kullanıcıya açar.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/logout").permitAll()

                        // ROUTES
                        .pathMatchers(HttpMethod.POST, "/api/routes/preview").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/routes/*").authenticated()
                        .pathMatchers("/api/routes/*/places/**").authenticated()

                        // TRIPS
                        .pathMatchers("/api/trips/**").authenticated()

                        // AI (web BFF)
                        .pathMatchers("/api/ai/**").authenticated()

                        // AI (mobile BFF) – ÖNEMLİ: mobile path burası
                        .pathMatchers("/api/mobile/**").authenticated()

                        // Analytics
                        .pathMatchers("/api/analytics/**").authenticated()

                        // Geri kalan her yer kapalı
                        .anyExchange().denyAll()
                )
                // Resource server: JWT doğrulama (issuer / jwk-set-uri ayarlarını application-local.yml'de verdik)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .build();
    }
}
