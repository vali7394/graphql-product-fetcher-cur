package com.example.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class Resilience4jConfig {

    private final AppProperties appProperties;

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(appProperties.getGraphql().getRateLimitPerSec())
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .timeoutDuration(Duration.ofSeconds(1))
            .build();

        return RateLimiterRegistry.of(config);
    }

    @Bean
    public RateLimiter graphQLRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("graphQLRateLimiter");
    }
} 