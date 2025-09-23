package com.safra.bank.device;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Device Registration API Spring Boot Application
 * 
 * Internal API for device registration operations
 * Part of Safra Bank's device tracking DevSecOps solution
 * 
 * DevSecOps Features:
 * - Internal-only access (not publicly exposed)
 * - Entity scanning from shared models for microservice architecture
 * - JPA repositories for secure database operations
 * - Component scanning for proper dependency injection
 * 
 * Architecture Notes:
 * - This API is internally accessible only (within cluster/network)
 * - Called by StatisticsAPI for device registration operations
 * - Implements proper logging and monitoring for banking security
 * - Handles database operations with transactional integrity
 * - Security hardening with updated Spring Boot 3.4.5 and dependencies
 */
@SpringBootApplication(scanBasePackages = {
    "com.safra.bank.device",
    "com.safra.bank.shared"
})
@EntityScan(basePackages = "com.safra.bank.shared.entity")
@EnableJpaRepositories(basePackages = "com.safra.bank.device.repository")
public class DeviceRegistrationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceRegistrationApiApplication.class, args);
    }
}