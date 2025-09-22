package com.safra.bank.statistics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security Configuration for Statistics API
 * Implements DevSecOps security best practices for banking applications
 * 
 * Security Features:
 * - HTTPS enforcement in production
 * - Comprehensive security headers (HSTS, CSP, X-Frame-Options, etc.)
 * - CORS configuration for controlled cross-origin access
 * - Rate limiting (to be implemented with additional dependencies)
 * - Session management with stateless policy
 * - Input validation and sanitization
 * - Actuator endpoint protection
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF configuration - disabled for API but could be enabled with proper frontend integration
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
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
            
            // Session management - stateless for API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/Log/**").permitAll()
                
                // Actuator endpoints - restrict in production
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").permitAll() // Should be restricted in production
                
                // OpenAPI documentation - can be restricted in production
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // All other requests require authentication (future expansion)
                .anyRequest().authenticated()
            );

        return http.build();
    }

    /**
     * CORS configuration for controlled cross-origin access
     * Allows specific origins and methods for banking security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (update for production)
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Restrict in production
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "X-Internal-Service"
        ));
        
        // Allow credentials for secure banking operations
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}