package com.safra.bank.device.service;

import com.safra.bank.device.repository.DeviceRegistrationRepository;
import com.safra.bank.shared.dto.DeviceRegistrationRequest;
import com.safra.bank.shared.dto.DeviceRegistrationResponse;
import com.safra.bank.shared.entity.DeviceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service layer for Device Registration API business logic
 * Handles device registration operations with data integrity
 * 
 * DevSecOps Features:
 * - Transactional operations for data consistency
 * - Comprehensive logging for audit trails
 * - Input validation and sanitization
 * - Exception handling with proper error responses
 * - No sensitive data exposure in logs
 * - Duplicate prevention with upsert behavior
 */
@Service
@Transactional
public class DeviceRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationService.class);

    @Autowired
    private DeviceRegistrationRepository deviceRegistrationRepository;

    /**
     * Register a device for a user
     * Implements upsert behavior - creates new registration or updates existing one
     * 
     * @param request the device registration request
     * @return DeviceRegistrationResponse indicating success or failure
     */
    public DeviceRegistrationResponse registerDevice(DeviceRegistrationRequest request) {
        try {
            logger.info("Processing device registration for device type: {}", request.getDeviceType());
            
            // Input validation (additional layer beyond controller validation)
            if (request.getUserKey() == null || request.getUserKey().trim().isEmpty()) {
                logger.warn("Device registration attempt with empty user key");
                return DeviceRegistrationResponse.badRequest();
            }
            
            if (request.getDeviceType() == null || 
                !isValidDeviceType(request.getDeviceType())) {
                logger.warn("Device registration attempt with invalid device type: {}", request.getDeviceType());
                return DeviceRegistrationResponse.badRequest();
            }

            // Check if registration already exists
            Optional<DeviceRegistration> existingRegistration = 
                deviceRegistrationRepository.findByUserKeyAndDeviceType(
                    request.getUserKey(), request.getDeviceType());

            DeviceRegistration registration;
            if (existingRegistration.isPresent()) {
                // Update existing registration
                registration = existingRegistration.get();
                logger.debug("Updating existing registration for user and device type");
            } else {
                // Create new registration
                registration = new DeviceRegistration(
                    request.getUserKey(), 
                    request.getDeviceType()
                );
                logger.debug("Creating new registration for user and device type");
            }

            // Save registration (insert or update)
            DeviceRegistration savedRegistration = deviceRegistrationRepository.save(registration);
            
            logger.info("Successfully registered device. ID: {}, Device Type: {}", 
                       savedRegistration.getId(), savedRegistration.getDeviceType());
            
            return DeviceRegistrationResponse.success();
            
        } catch (DataIntegrityViolationException e) {
            // Handle database constraint violations
            logger.warn("Data integrity violation during device registration: {}", e.getMessage());
            return DeviceRegistrationResponse.badRequest();
            
        } catch (Exception e) {
            logger.error("Error registering device: {}", e.getMessage(), e);
            return DeviceRegistrationResponse.internalError();
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