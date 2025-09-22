package com.safra.bank.statistics.repository;

import com.safra.bank.shared.entity.DeviceRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for DeviceRegistration entity operations
 * Used by StatisticsAPI for querying device statistics
 * 
 * DevSecOps Features:
 * - Uses parameterized queries to prevent SQL injection
 * - Read-only operations for security (statistics don't modify data)
 * - Optimized queries for performance in high-traffic banking environment
 */
@Repository
public interface DeviceRegistrationRepository extends JpaRepository<DeviceRegistration, Long> {

    /**
     * Count devices registered for a specific device type
     * Uses parameterized query to prevent SQL injection attacks
     * 
     * @param deviceType the device type to count (iOS, Android, Watch, TV)
     * @return count of registered devices for the specified type
     */
    @Query("SELECT COUNT(d) FROM DeviceRegistration d WHERE d.deviceType = :deviceType")
    Long countByDeviceType(@Param("deviceType") String deviceType);

    /**
     * Check if a user has already registered a specific device type
     * Used for preventing duplicate registrations
     * 
     * @param userKey the user identifier
     * @param deviceType the device type
     * @return true if registration exists, false otherwise
     */
    boolean existsByUserKeyAndDeviceType(String userKey, String deviceType);
}