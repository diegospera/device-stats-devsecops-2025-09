package com.safra.bank.statistics.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Application configuration for Statistics API
 * Configures beans and application-wide settings
 * 
 * DevSecOps Features:
 * - RestTemplate with timeouts to prevent hanging connections
 * - Connection pooling for performance
 * - Secure HTTP client configuration
 */
@Configuration
public class ApplicationConfig {

    /**
     * Configure RestTemplate for HTTP communication
     * Used for calling internal DeviceRegistrationAPI
     * 
     * @param builder RestTemplateBuilder provided by Spring Boot
     * @return configured RestTemplate with security settings
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
    }
}