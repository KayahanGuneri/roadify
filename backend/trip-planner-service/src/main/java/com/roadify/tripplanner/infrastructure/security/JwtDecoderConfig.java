package com.roadify.tripplanner.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.List;

@Configuration
public class JwtDecoderConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Default validators: timestamp (exp/nbf) vb.
        OAuth2TokenValidator<Jwt> withTimestamp = JwtValidators.createDefault();

        // Burada issuer validator EKLEMİYORUZ (iss farklı olduğu için).
        // İstersen ayrıca audience/azp gibi claim validator ekleyebiliriz.
        decoder.setJwtValidator(withTimestamp);

        return decoder;
    }
}
