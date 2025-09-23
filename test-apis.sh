#!/bin/bash

# Safra Bank Device Statistics API Testing Script
# DevSecOps validation script for the complete solution

set -e

echo "🏦 Safra Bank Device Statistics API Testing"
echo "============================================"

# Configuration - Auto-detect production vs development setup
if docker-compose -f docker-compose.prod.yml ps 2>/dev/null | grep -q "safra-nginx.*Up"; then
    echo "🔍 Detected production setup (Nginx reverse proxy)"
    STATISTICS_API_URL="${STATISTICS_API_URL:-http://localhost/api/statistics}"
    DEVICE_REG_API_URL="${DEVICE_REG_API_URL:-http://localhost/api/internal}"
    NGINX_PROXY=true
elif docker-compose ps 2>/dev/null | grep -q "Up"; then
    echo "🔍 Detected development setup (direct service access)"
    STATISTICS_API_URL="${STATISTICS_API_URL:-http://localhost:8080}"
    DEVICE_REG_API_URL="${DEVICE_REG_API_URL:-http://localhost:8081}"
    NGINX_PROXY=false
else
    echo "🔍 Using manual configuration"
    STATISTICS_API_URL="${STATISTICS_API_URL:-http://localhost:8080}"
    DEVICE_REG_API_URL="${DEVICE_REG_API_URL:-http://localhost:8081}"
    NGINX_PROXY=false
fi

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to test API health
test_health() {
    local api_name=$1
    local api_url=$2

    echo -e "${BLUE}Testing $api_name health...${NC}"

    if curl -s -f "$api_url/actuator/health" > /dev/null; then
        echo -e "${GREEN}✓ $api_name is healthy${NC}"
        return 0
    else
        echo -e "${RED}✗ $api_name is not responding${NC}"
        if [ "$NGINX_PROXY" = true ]; then
            echo -e "${YELLOW}  Note: Using Nginx reverse proxy setup${NC}"
        fi
        return 1
    fi
}

# Function to test device registration via Statistics API
test_device_registration() {
    local user_key=$1
    local device_type=$2
    
    echo -e "${BLUE}Testing device registration: $user_key -> $device_type${NC}"
    
    local response=$(curl -s -X POST "$STATISTICS_API_URL/Log/auth" \
        -H "Content-Type: application/json" \
        -d "{\"userKey\": \"$user_key\", \"deviceType\": \"$device_type\"}")
    
    local status_code=$(echo "$response" | grep -o '"statusCode":[0-9]*' | cut -d':' -f2)
    
    if [ "$status_code" = "200" ]; then
        echo -e "${GREEN}✓ Successfully registered $device_type for $user_key${NC}"
        return 0
    else
        echo -e "${RED}✗ Failed to register device. Response: $response${NC}"
        return 1
    fi
}

# Function to test statistics retrieval
test_statistics() {
    local device_type=$1
    
    echo -e "${BLUE}Testing statistics retrieval for: $device_type${NC}"
    
    local response=$(curl -s "$STATISTICS_API_URL/Log/auth/statistics?deviceType=$device_type")
    local count=$(echo "$response" | grep -o '"count":[0-9]*' | cut -d':' -f2)
    
    if [ ! -z "$count" ] && [ "$count" -ge 0 ]; then
        echo -e "${GREEN}✓ Retrieved statistics: $device_type has $count registrations${NC}"
        return 0
    else
        echo -e "${RED}✗ Failed to retrieve statistics. Response: $response${NC}"
        return 1
    fi
}

# Main test execution
main() {
    echo -e "${YELLOW}Starting DevSecOps API validation...${NC}"
    echo ""
    
    # Test 1: Health checks
    echo "🔍 Phase 1: Health Checks"
    test_health "Statistics API" "$STATISTICS_API_URL" || exit 1
    test_health "Device Registration API" "$DEVICE_REG_API_URL" || exit 1
    echo ""
    
    # Test 2: Device registrations
    echo "📱 Phase 2: Device Registration Tests"
    test_device_registration "testuser1" "iOS" || exit 1
    test_device_registration "testuser1" "Watch" || exit 1
    test_device_registration "testuser2" "Android" || exit 1
    test_device_registration "testuser3" "TV" || exit 1
    test_device_registration "testuser4" "iOS" || exit 1
    echo ""
    
    # Test 3: Statistics retrieval
    echo "📊 Phase 3: Statistics Retrieval Tests"
    test_statistics "iOS" || exit 1
    test_statistics "Android" || exit 1
    test_statistics "Watch" || exit 1
    test_statistics "TV" || exit 1
    echo ""
    
    # Test 4: Error handling
    echo "🚨 Phase 4: Error Handling Tests"
    echo -e "${BLUE}Testing invalid device type...${NC}"
    local error_response=$(curl -s "$STATISTICS_API_URL/Log/auth/statistics?deviceType=InvalidType")
    if echo "$error_response" | grep -q '"count":-1'; then
        echo -e "${GREEN}✓ Error handling works correctly${NC}"
    else
        echo -e "${RED}✗ Error handling test failed${NC}"
        exit 1
    fi
    echo ""
    
    echo -e "${GREEN}🎉 All tests passed! DevSecOps solution is working correctly.${NC}"
    echo ""
    echo "📋 Test Summary:"
    echo "  • Health checks: ✓"
    echo "  • Device registration: ✓"
    echo "  • Statistics retrieval: ✓"
    echo "  • Error handling: ✓"
    echo ""
    echo -e "${BLUE}🔗 Useful URLs:${NC}"
    echo "  • Statistics API Swagger: $STATISTICS_API_URL/swagger-ui.html"
    echo "  • Device Registration API Swagger: $DEVICE_REG_API_URL/swagger-ui.html"
    echo "  • Statistics API Health: $STATISTICS_API_URL/actuator/health"
    echo "  • Device Registration API Health: $DEVICE_REG_API_URL/actuator/health"
    echo ""
    echo -e "${GREEN}Ready for Senior DevSecOps Engineer interview! 🚀${NC}"
}

# Execute main function
main "$@"