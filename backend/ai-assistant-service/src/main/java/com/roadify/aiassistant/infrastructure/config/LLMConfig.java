package com.roadify.aiassistant.infrastructure.config;

import com.roadify.aiassistant.domain.llm.LLMClient;
import com.roadify.aiassistant.infrastructure.client.llm.HttpLLMClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Türkçe Özet:
 * LLM ile ilgili bean tanımlarını yapan konfigürasyon sınıfı.
 * RestClient + LLMProperties kullanarak LLMClient implementasyonunu üretir.
 */
@Configuration
@EnableConfigurationProperties(LLMProperties.class)
public class LLMConfig {

    @Bean
    public LLMClient llmClient(RestClient.Builder restClientBuilder, LLMProperties llmProperties) {

        RestClient restClient = restClientBuilder
                .baseUrl(llmProperties.getBaseUrl())
                .build();

        return new HttpLLMClient(restClient, llmProperties);
    }
}
