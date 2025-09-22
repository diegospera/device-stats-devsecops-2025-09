package com.safra.bank.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA Entity representing a device registration for a user
 * Stores user login events with their device type information
 * 
 * DevSecOps Notes:
 * - Uses validation annotations to prevent injection attacks
 * - Implements proper equals/hashCode for entity integrity
 * - Includes audit fields for security tracking
 */
@Entity
@Table(name = "device_registrations", 
       indexes = {
           @Index(name = "idx_device_type", columnList = "deviceType"),
           @Index(name = "idx_user_key", columnList = "userKey"),
           @Index(name = "idx_created_at", columnList = "createdAt")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_device", 
                           columnNames = {"userKey", "deviceType"})
       })
public class DeviceRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User key cannot be blank")
    @Column(name = "userKey", nullable = false, length = 255)
    private String userKey;

    @NotBlank(message = "Device type cannot be blank")
    @Pattern(regexp = "^(iOS|Android|Watch|TV)$", 
             message = "Device type must be one of: iOS, Android, Watch, TV")
    @Column(name = "deviceType", nullable = false, length = 50)
    private String deviceType;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public DeviceRegistration() {}

    public DeviceRegistration(String userKey, String deviceType) {
        this.userKey = userKey;
        this.deviceType = deviceType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceRegistration that = (DeviceRegistration) o;
        return Objects.equals(userKey, that.userKey) &&
               Objects.equals(deviceType, that.deviceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userKey, deviceType);
    }

    @Override
    public String toString() {
        return "DeviceRegistration{" +
                "id=" + id +
                ", userKey='" + userKey + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}