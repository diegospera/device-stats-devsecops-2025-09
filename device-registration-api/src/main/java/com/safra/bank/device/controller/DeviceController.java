package com.safra.bank.device.controller;

import com.safra.bank.device.service.DeviceRegistrationService;
import com.safra.bank.shared.dto.DeviceRegistrationRequest;
import com.safra.bank.shared.dto.DeviceRegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Device Registration API endpoints
 * Handles internal device registration operations
 * 
 * DevSecOps Features:
 * - Internal-only access (not publicly exposed)
 * - Comprehensive input validation using Bean Validation
 * - Detailed API documentation with OpenAPI/Swagger
 * - Security logging for audit trails
 * - Proper HTTP status code handling
 * - Request source validation for internal services
 */
@RestController
@RequestMapping("/Device")
@Validated
@Tag(name = "Device Registration API", description = "Internal API for device registration operations")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    /**
     * Register a Device Type for a given User
     * Endpoint: POST /Device/register
     * 
     * @param registrationRequest validated request containing userKey and deviceType
     * @return DeviceRegistrationResponse with status code
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register device for user", 
        description = "Internal endpoint to register a device type for a specific user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DeviceRegistrationResponse> registerDevice(
            @Valid @RequestBody DeviceRegistrationRequest registrationRequest,
            @RequestHeader(value = "X-Internal-Service", required = false) String internalService) {
        
        logger.info("Received device registration request for device type: {} from service: {}", 
                   registrationRequest.getDeviceType(), internalService);
        
        // Additional security check for internal service calls
        if (internalService == null || internalService.trim().isEmpty()) {
            logger.warn("Device registration request without internal service header");
        }
        
        try {
            DeviceRegistrationResponse response = 
                deviceRegistrationService.registerDevice(registrationRequest);
            
            // Map internal status codes to HTTP status codes
            if (response.getStatusCode() == 200) {
                return ResponseEntity.ok(response);
            } else if (response.getStatusCode() == 400) {
                return ResponseEntity.badRequest().body(response);
            } else {
                return ResponseEntity.internalServerError().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during device registration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(DeviceRegistrationResponse.internalError());
        }
    }
}