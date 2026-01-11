package com.roadify.aiassistant.infrastructure.config;

import com.roadify.aiassistant.api.logging.LoggingContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Türkçe Özet:
 * MDC için LoggingContextFilter'ı Spring Boot filter chain'ine ekler.
 */
@Configuration
public class LoggingConfig {

    @Bean
    public FilterRegistrationBean<LoggingContextFilter> loggingContextFilterRegistration() {
        FilterRegistrationBean<LoggingContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoggingContextFilter());
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
