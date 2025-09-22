package com.safra.bank.statistics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for Statistics API
 * Provides comprehensive API documentation for DevSecOps practices
 * 
 * Features:
 * - Interactive API documentation
 * - Security schema documentation
 * - Environment-specific server configuration
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI statisticsApiOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Development server");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.safra.bank");
        prodServer.setDescription("Production server");

        Contact contact = new Contact();
        contact.setEmail("devsecops@safra.bank");
        contact.setName("Safra Bank DevSecOps Team");

        License license = new License()
            .name("Proprietary")
            .url("https://safra.bank/license");

        Info info = new Info()
            .title("Safra Bank Statistics API")
            .version("1.0.0")
            .contact(contact)
            .description("Public API for handling user login events and device statistics. " +
                        "Part of Safra Bank's device tracking DevSecOps solution.")
            .license(license);

        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer, prodServer));
    }
}