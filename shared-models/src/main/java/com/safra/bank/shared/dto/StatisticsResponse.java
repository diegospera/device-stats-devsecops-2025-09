package com.safra.bank.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for device statistics responses
 * Used by StatisticsAPI GET /Log/auth/statistics endpoint
 * 
 * DevSecOps Features:
 * - Standardized response format
 * - Type-safe integer count field
 * - Error indication with -1 count value
 */
public class StatisticsResponse {

    @JsonProperty("deviceType")
    private String deviceType;

    @JsonProperty("count")
    private Integer count;

    // Default constructor for JSON serialization
    public StatisticsResponse() {}

    public StatisticsResponse(String deviceType, Integer count) {
        this.deviceType = deviceType;
        this.count = count;
    }

    // Factory method for error response
    public static StatisticsResponse error(String deviceType) {
        return new StatisticsResponse(deviceType, -1);
    }

    // Factory method for success response
    public static StatisticsResponse success(String deviceType, Integer count) {
        return new StatisticsResponse(deviceType, count);
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "deviceType='" + deviceType + '\'' +
                ", count=" + count +
                '}';
    }
}