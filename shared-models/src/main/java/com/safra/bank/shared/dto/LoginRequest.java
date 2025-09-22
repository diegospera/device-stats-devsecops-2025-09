package com.safra.bank.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for user login authentication requests
 * Used by StatisticsAPI POST /Log/auth endpoint
 * 
 * DevSecOps Features:
 * - Input validation to prevent injection attacks
 * - Size constraints to prevent buffer overflow
 * - Pattern validation for device type enumeration
 * - JSON property mapping for consistent API contract
 */
public class LoginRequest {

    @NotBlank(message = "User key is required")
    @Size(min = 1, max = 255, message = "User key must be between 1 and 255 characters")
    @JsonProperty("userKey")
    private String userKey;

    @NotBlank(message = "Device type is required")
    @Pattern(regexp = "^(iOS|Android|Watch|TV)$", 
             message = "Device type must be one of: iOS, Android, Watch, TV")
    @JsonProperty("deviceType")
    private String deviceType;

    // Default constructor for JSON deserialization
    public LoginRequest() {}

    public LoginRequest(String userKey, String deviceType) {
        this.userKey = userKey;
        this.deviceType = deviceType;
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

    @Override
    public String toString() {
        return "LoginRequest{" +
                "userKey='" + userKey + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}