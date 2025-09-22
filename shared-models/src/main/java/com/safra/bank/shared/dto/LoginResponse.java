package com.safra.bank.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for login authentication responses
 * Used by StatisticsAPI POST /Log/auth endpoint
 * 
 * DevSecOps Features:
 * - Standardized response format
 * - Clear status code mapping
 * - Secure error messaging (no sensitive data exposure)
 */
public class LoginResponse {

    @JsonProperty("statusCode")
    private Integer statusCode;

    @JsonProperty("message")
    private String message;

    // Default constructor for JSON serialization
    public LoginResponse() {}

    public LoginResponse(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    // Factory methods for common responses
    public static LoginResponse success() {
        return new LoginResponse(200, "success");
    }

    public static LoginResponse badRequest() {
        return new LoginResponse(400, "bad_request");
    }

    public static LoginResponse internalError() {
        return new LoginResponse(500, "internal_error");
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}