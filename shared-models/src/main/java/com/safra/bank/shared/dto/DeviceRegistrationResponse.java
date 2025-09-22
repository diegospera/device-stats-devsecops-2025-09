package com.safra.bank.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for device registration responses
 * Used by DeviceRegistrationAPI POST /Device/register endpoint
 * 
 * DevSecOps Features:
 * - Standardized response format
 * - Clear status code mapping
 * - Secure error messaging (no sensitive data exposure)
 */
public class DeviceRegistrationResponse {

    @JsonProperty("statusCode")
    private Integer statusCode;

    // Default constructor for JSON serialization
    public DeviceRegistrationResponse() {}

    public DeviceRegistrationResponse(Integer statusCode) {
        this.statusCode = statusCode;
    }

    // Factory methods for common responses
    public static DeviceRegistrationResponse success() {
        return new DeviceRegistrationResponse(200);
    }

    public static DeviceRegistrationResponse badRequest() {
        return new DeviceRegistrationResponse(400);
    }

    public static DeviceRegistrationResponse internalError() {
        return new DeviceRegistrationResponse(500);
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "DeviceRegistrationResponse{" +
                "statusCode=" + statusCode +
                '}';
    }
}