package com.roadify.route.infrastructure.cache;

import com.roadify.route.domain.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration for caching Route objects.
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Route> routeRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Route> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Keys as plain strings
        template.setKeySerializer(new StringRedisSerializer());

        // Values as JSON-serialized Route objects
        Jackson2JsonRedisSerializer<Route> valueSerializer =
                new Jackson2JsonRedisSerializer<>(Route.class);
        template.setValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
