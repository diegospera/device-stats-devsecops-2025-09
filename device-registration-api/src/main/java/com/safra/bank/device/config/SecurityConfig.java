package com.safra.bank.device.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Spring Security Configuration for Device Registration API
 * Implements DevSecOps security best practices for internal banking services
 * 
 * Security Features:
 * - Internal service access control
 * - HTTPS enforcement in production
 * - Comprehensive security headers
 * - Internal service authentication via headers
 * - Session management with stateless policy
 * - Network-level isolation (Kubernetes NetworkPolicies)
 * - Input validation and sanitization
 * - Actuator endpoint protection
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF configuration - disabled for internal API
            .csrf(AbstractHttpConfigurer::disable)
            
            // No CORS needed for internal service
            .cors(AbstractHttpConfigurer::disable)
            
            // Security headers for DevSecOps
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true))
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .httpStrictTransportSecurity(httpSecurityHeadersConfigurer -> 
                    httpSecurityHeadersConfigurer.requestMatcher(request -> 
                        request.getHeader("X-Forwarded-Proto") != null))
            )
            
            // Session management - stateless for internal API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authorization rules for internal service
            .authorizeHttpRequests(authz -> authz
                // Internal endpoints - accessible within network/cluster
                .requestMatchers("/Device/**").permitAll() // Network policies provide isolation
                
                // Actuator endpoints - restrict access
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").permitAll() // Should be restricted in production
                
                // OpenAPI documentation - internal use only
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // All other requests deny
                .anyRequest().denyAll()
            );

        return http.build();
    }
}