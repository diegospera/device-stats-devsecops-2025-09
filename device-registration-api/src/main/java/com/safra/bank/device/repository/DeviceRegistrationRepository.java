package com.safra.bank.device.repository;

import com.safra.bank.shared.entity.DeviceRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for DeviceRegistration entity operations
 * Used by DeviceRegistrationAPI for device registration persistence
 * 
 * DevSecOps Features:
 * - Transactional database operations for data integrity
 * - Optimized queries for duplicate prevention
 * - Secure parameterized queries to prevent SQL injection
 */
@Repository
public interface DeviceRegistrationRepository extends JpaRepository<DeviceRegistration, Long> {

    /**
     * Find existing device registration for a user and device type
     * Used to prevent duplicate registrations and support upsert operations
     * 
     * @param userKey the user identifier
     * @param deviceType the device type
     * @return Optional containing the registration if it exists
     */
    Optional<DeviceRegistration> findByUserKeyAndDeviceType(String userKey, String deviceType);

    /**
     * Check if a registration already exists for user and device type
     * Optimized query for existence checking without fetching full entity
     * 
     * @param userKey the user identifier
     * @param deviceType the device type
     * @return true if registration exists, false otherwise
     */
    boolean existsByUserKeyAndDeviceType(String userKey, String deviceType);
}