package com.roadify.aiassistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Local dev: AI Assistant servisi sadece Gateway'den çağrıldığı için
 * burada ekstra JWT doğrulaması yapmıyoruz.
 * Gateway token'ı validate ediyor, bu servis sadece iş mantığını çalıştırıyor.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Actuator health/info açık kalsın
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // AI chat endpoint'i gateway'den serbest gelsin
                        .requestMatchers(HttpMethod.POST, "/v1/ai/chat").permitAll()

                        // Geri kalan her şeyi kapat
                        .anyRequest().denyAll()
                );

        // Bu serviste JWT resource server yok, sadece basic filter chain
        return http.build();
    }
}
