package com.safra.bank.statistics.service;

import com.safra.bank.shared.dto.*;
import com.safra.bank.statistics.repository.DeviceRegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service layer for Statistics API business logic
 * Handles user login events and device statistics retrieval
 * 
 * DevSecOps Features:
 * - Comprehensive logging for audit trails
 * - Input validation and sanitization
 * - Secure communication with internal APIs
 * - Exception handling with proper error responses
 * - No sensitive data exposure in logs
 */
@Service
public class StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    @Autowired
    private DeviceRegistrationRepository deviceRegistrationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${device.registration.api.url:http://localhost:8081}")
    private String deviceRegistrationApiUrl;

    /**
     * Process user login event and register device
     * Communicates with DeviceRegistrationAPI to store the registration
     * 
     * @param loginRequest the login request containing user and device information
     * @return LoginResponse indicating success or failure
     */
    public LoginResponse processLogin(LoginRequest loginRequest) {
        try {
            logger.info("Processing login for device type: {}", loginRequest.getDeviceType());
            
            // Input validation (additional layer beyond controller validation)
            if (loginRequest.getUserKey() == null || loginRequest.getUserKey().trim().isEmpty()) {
                logger.warn("Login attempt with empty user key");
                return LoginResponse.badRequest();
            }
            
            if (loginRequest.getDeviceType() == null || 
                !isValidDeviceType(loginRequest.getDeviceType())) {
                logger.warn("Login attempt with invalid device type: {}", loginRequest.getDeviceType());
                return LoginResponse.badRequest();
            }

            // Create device registration request
            DeviceRegistrationRequest registrationRequest = new DeviceRegistrationRequest(
                loginRequest.getUserKey(),
                loginRequest.getDeviceType()
            );

            // Call DeviceRegistrationAPI
            DeviceRegistrationResponse registrationResponse = callDeviceRegistrationAPI(registrationRequest);
            
            if (registrationResponse != null && registrationResponse.getStatusCode() == 200) {
                logger.info("Successfully processed login for device type: {}", loginRequest.getDeviceType());
                return LoginResponse.success();
            } else {
                logger.error("Failed to register device. Response: {}", registrationResponse);
                return LoginResponse.badRequest();
            }
            
        } catch (Exception e) {
            logger.error("Error processing login: {}", e.getMessage(), e);
            return LoginResponse.internalError();
        }
    }

    /**
     * Retrieve device statistics for a specific device type
     * 
     * @param deviceType the device type to get statistics for
     * @return StatisticsResponse with device count or error indication
     */
    public StatisticsResponse getDeviceStatistics(String deviceType) {
        try {
            logger.info("Retrieving statistics for device type: {}", deviceType);

            // Check if device type is valid
            if (deviceType == null || !isValidDeviceType(deviceType)) {
                logger.warn("Statistics request with invalid device type: {}", deviceType);
                // Return count -1 for invalid device types as per task requirements
                return StatisticsResponse.error(deviceType);
            }

            // Query database for device count
            Long count = deviceRegistrationRepository.countByDeviceType(deviceType);

            logger.info("Found {} registrations for device type: {}", count, deviceType);
            return StatisticsResponse.success(deviceType, count.intValue());

        } catch (Exception e) {
            logger.error("Error retrieving statistics for device type {}: {}", deviceType, e.getMessage(), e);
            return StatisticsResponse.error(deviceType);
        }
    }

    /**
     * Call the internal DeviceRegistrationAPI to register a device
     * Uses secure HTTP communication with proper headers
     * 
     * @param request the device registration request
     * @return DeviceRegistrationResponse or null if call fails
     */
    private DeviceRegistrationResponse callDeviceRegistrationAPI(DeviceRegistrationRequest request) {
        try {
            String url = deviceRegistrationApiUrl + "/Device/register";
            
            // Set headers for secure communication
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-Service", "statistics-api");
            
            HttpEntity<DeviceRegistrationRequest> entity = new HttpEntity<>(request, headers);
            
            logger.debug("Calling DeviceRegistrationAPI at: {}", url);
            
            ResponseEntity<DeviceRegistrationResponse> response = restTemplate.postForEntity(
                url, entity, DeviceRegistrationResponse.class);
            
            return response.getBody();
            
        } catch (RestClientException e) {
            logger.error("Failed to call DeviceRegistrationAPI: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validate device type against allowed values
     * Prevents injection attacks and ensures data integrity
     * 
     * @param deviceType the device type to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidDeviceType(String deviceType) {
        return deviceType != null && 
               (deviceType.equals("iOS") || 
                deviceType.equals("Android") || 
                deviceType.equals("Watch") || 
                deviceType.equals("TV"));
    }
}