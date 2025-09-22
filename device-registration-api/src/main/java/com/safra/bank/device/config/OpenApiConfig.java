package com.safra.bank.device.config;

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
 * OpenAPI/Swagger configuration for Device Registration API
 * Provides internal API documentation for DevSecOps practices
 * 
 * Features:
 * - Internal service documentation
 * - Security schema documentation
 * - Environment-specific server configuration
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI deviceRegistrationApiOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Development server");

        Server internalServer = new Server();
        internalServer.setUrl("http://device-registration-api:8081");
        internalServer.setDescription("Internal service");

        Contact contact = new Contact();
        contact.setEmail("devsecops@safra.bank");
        contact.setName("Safra Bank DevSecOps Team");

        License license = new License()
            .name("Proprietary")
            .url("https://safra.bank/license");

        Info info = new Info()
            .title("Safra Bank Device Registration API")
            .version("1.0.0")
            .contact(contact)
            .description("Internal API for device registration operations. " +
                        "This service handles device registration requests from the Statistics API. " +
                        "NOT PUBLICLY ACCESSIBLE - Internal use only.")
            .license(license);

        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer, internalServer));
    }
}