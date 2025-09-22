package com.safra.bank.statistics.controller;

// Statistics API Controller - Handles user login events and device statistics
// Updated for CI/CD pipeline testing - multi-platform build

import com.safra.bank.shared.dto.LoginRequest;
import com.safra.bank.shared.dto.LoginResponse;
import com.safra.bank.shared.dto.StatisticsResponse;
import com.safra.bank.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Statistics API endpoints
 * Handles user login events and device statistics retrieval
 * 
 * DevSecOps Features:
 * - Comprehensive input validation using Bean Validation
 * - Detailed API documentation with OpenAPI/Swagger
 * - Security logging for audit trails
 * - Proper HTTP status code handling
 * - Rate limiting and security headers (configured in security config)
 */
@RestController
@RequestMapping("/Log")
@Validated
@Tag(name = "Statistics API", description = "Public API for device statistics and user login events")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private StatisticsService statisticsService;

    /**
     * Store information about user login event
     * Endpoint: POST /Log/auth
     * 
     * @param loginRequest validated request containing userKey and deviceType
     * @return LoginResponse with status code and message
     */
    @PostMapping("/auth")
    @Operation(
        summary = "Process user login event", 
        description = "Stores user login event and registers device type in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoginResponse> processLogin(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        logger.info("Received login request for device type: {}", loginRequest.getDeviceType());
        
        try {
            LoginResponse response = statisticsService.processLogin(loginRequest);
            
            // Map internal status codes to HTTP status codes
            if (response.getStatusCode() == 200) {
                return ResponseEntity.ok(response);
            } else if (response.getStatusCode() == 400) {
                return ResponseEntity.badRequest().body(response);
            } else {
                return ResponseEntity.internalServerError().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error processing login: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(LoginResponse.internalError());
        }
    }

    /**
     * Retrieve Device Registrations by Type
     * Endpoint: GET /Log/auth/statistics
     * 
     * @param deviceType the device type to get statistics for (iOS, Android, Watch, TV)
     * @return StatisticsResponse with device type and count
     */
    @GetMapping("/auth/statistics")
    @Operation(
        summary = "Get device statistics", 
        description = "Retrieves the count of registered devices for a specific device type"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid device type"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StatisticsResponse> getDeviceStatistics(
            @Parameter(description = "Device type (iOS, Android, Watch, TV)", required = true)
            @RequestParam("deviceType")
            String deviceType) {
        
        logger.info("Received statistics request for device type: {}", deviceType);
        
        try {
            StatisticsResponse response = statisticsService.getDeviceStatistics(deviceType);

            // Always return 200 OK with the response (count = -1 indicates error)
            // This matches the task requirements which expect JSON response even for invalid device types
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Unexpected error retrieving statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(StatisticsResponse.error(deviceType));
        }
    }
}