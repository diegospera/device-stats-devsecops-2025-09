# Multi-stage Dockerfile for Safra Bank Statistics API
# DevSecOps optimized with security best practices

# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Set build arguments for security
ARG BUILD_DATE
ARG VCS_REF
ARG VERSION=1.0.0

# Add metadata for security and compliance
LABEL org.opencontainers.image.title="Safra Bank Statistics API"
LABEL org.opencontainers.image.description="Public API for device statistics and user login events"
LABEL org.opencontainers.image.vendor="Safra Bank"
LABEL org.opencontainers.image.version=${VERSION}
LABEL org.opencontainers.image.created=${BUILD_DATE}
LABEL org.opencontainers.image.source="https://github.com/safra-bank/device-stats-devsecops"
LABEL org.opencontainers.image.revision=${VCS_REF}

# Set working directory
WORKDIR /app

# Copy all project files (need full multi-module structure)
COPY pom.xml ./
COPY shared-models/ ./shared-models/
COPY statistics-api/ ./statistics-api/
COPY device-registration-api/ ./device-registration-api/

# Build only the statistics-api module
RUN mvn clean package -B -Dmaven.test.skip=true -pl statistics-api -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install security updates and create non-root user
RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
        curl \
        ca-certificates && \
    rm -rf /var/cache/apk/* && \
    addgroup -g 1001 -S safra && \
    adduser -u 1001 -S safra -G safra

# Set working directory
WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /app/statistics-api/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R safra:safra /app

# Switch to non-root user for security
USER safra

# Expose port
EXPOSE 8080

# Health check for container orchestration
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseCGroupMemoryLimitForHeap \
               -Djava.security.egd=file:/dev/./urandom"

# Run application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]