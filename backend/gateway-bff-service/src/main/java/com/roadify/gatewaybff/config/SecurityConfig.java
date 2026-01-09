package com.roadify.gatewaybff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${security.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        // Ops / health
                        .pathMatchers("/actuator/**").permitAll()

                        // Public routes
                        .pathMatchers(HttpMethod.POST, "/api/routes/preview").permitAll()
                        .pathMatchers(HttpMethod.GET,  "/api/routes/*").permitAll()

                        // Places by route
                        .pathMatchers(HttpMethod.GET,  "/api/routes/*/places/**").permitAll()
                        // >>> AI-assistant ve mobile tarafının kullandığı POST endpoint:
                        .pathMatchers(HttpMethod.POST, "/api/routes/*/places/**").authenticated()

                        // Protected areas
                        .pathMatchers("/api/trips/**").authenticated()
                        .pathMatchers("/api/ai/**").authenticated()
                        .pathMatchers("/api/analytics/**").authenticated()

                        // Safer default: deny all
                        .anyExchange().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuer);
        decoder.setJwtValidator(issuerValidator);
        return decoder;
    }
}
