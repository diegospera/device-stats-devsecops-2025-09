#!/bin/bash

# Manual Docker Build and Push Script
# Safra Bank Device Statistics DevSecOps Solution

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-"safrabank"}
IMAGE_TAG=${IMAGE_TAG:-"latest"}
BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ')
VCS_REF=$(git rev-parse --short HEAD)

echo -e "${BLUE}🏦 Safra Bank DevSecOps - Docker Build & Push${NC}"
echo "=============================================="
echo "DockerHub Username: $DOCKERHUB_USERNAME"
echo "Image Tag: $IMAGE_TAG"
echo "Build Date: $BUILD_DATE"
echo "VCS Ref: $VCS_REF"
echo ""

# Function to build and push an image
build_and_push() {
    local service=$1
    local dockerfile_path=$2
    local image_name="$DOCKERHUB_USERNAME/safra-$service"

    echo -e "${YELLOW}Building $service...${NC}"

    # Build the image
    docker build \
        --file "$dockerfile_path" \
        --tag "$image_name:$IMAGE_TAG" \
        --tag "$image_name:latest" \
        --build-arg BUILD_DATE="$BUILD_DATE" \
        --build-arg VCS_REF="$VCS_REF" \
        --build-arg VERSION="$IMAGE_TAG" \
        .

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Successfully built $service${NC}"

        # Push the image
        echo -e "${YELLOW}Pushing $service to DockerHub...${NC}"
        docker push "$image_name:$IMAGE_TAG"

        if [ "$IMAGE_TAG" != "latest" ]; then
            docker push "$image_name:latest"
        fi

        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ Successfully pushed $service${NC}"
        else
            echo -e "${RED}✗ Failed to push $service${NC}"
            return 1
        fi
    else
        echo -e "${RED}✗ Failed to build $service${NC}"
        return 1
    fi

    echo ""
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Check if user is logged in to DockerHub
if ! docker info | grep -q "Username"; then
    echo -e "${YELLOW}⚠ Not logged in to DockerHub. Please run: docker login${NC}"
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Determine which services to build
if [ "$1" = "statistics-api" ]; then
    build_and_push "statistics-api" "statistics-api/Dockerfile"
elif [ "$1" = "device-registration-api" ]; then
    build_and_push "device-registration-api" "device-registration-api/Dockerfile"
elif [ "$1" = "all" ] || [ -z "$1" ]; then
    echo -e "${BLUE}Building all services...${NC}"
    echo ""

    build_and_push "statistics-api" "statistics-api/Dockerfile"
    build_and_push "device-registration-api" "device-registration-api/Dockerfile"
else
    echo -e "${RED}✗ Unknown service: $1${NC}"
    echo "Usage: $0 [statistics-api|device-registration-api|all]"
    exit 1
fi

echo -e "${GREEN}🎉 Build and push completed successfully!${NC}"
echo ""
echo -e "${BLUE}📋 Images created:${NC}"
echo "  • $DOCKERHUB_USERNAME/safra-statistics-api:$IMAGE_TAG"
echo "  • $DOCKERHUB_USERNAME/safra-device-registration-api:$IMAGE_TAG"
echo ""
echo -e "${BLUE}🚀 To deploy with these images:${NC}"
echo "  export DOCKERHUB_USERNAME=$DOCKERHUB_USERNAME"
echo "  export IMAGE_TAG=$IMAGE_TAG"
echo "  docker-compose -f docker-compose.prod.yml up -d"