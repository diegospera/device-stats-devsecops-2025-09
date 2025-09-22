package com.safra.bank.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Statistics API Spring Boot Application
 * 
 * Public-facing API for handling user login events and device statistics
 * Part of Safra Bank's device tracking DevSecOps solution
 * 
 * DevSecOps Features:
 * - Entity scanning from shared models for microservice architecture
 * - JPA repositories for secure database operations
 * - Component scanning for proper dependency injection
 * 
 * Architecture Notes:
 * - This API is publicly accessible and acts as the entry point
 * - Communicates internally with DeviceRegistrationAPI
 * - Implements proper logging and monitoring for banking security
 */
@SpringBootApplication(scanBasePackages = {
    "com.safra.bank.statistics",
    "com.safra.bank.shared"
})
@EntityScan(basePackages = "com.safra.bank.shared.entity")
@EnableJpaRepositories(basePackages = "com.safra.bank.statistics.repository")
public class StatisticsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsApiApplication.class, args);
    }
}