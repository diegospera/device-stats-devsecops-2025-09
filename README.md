# Safra Bank Device Statistics DevSecOps Solution

## 🏦 Overview

This repository contains a comprehensive DevSecOps solution for Safra Bank's device statistics tracking system. The solution implements secure microservices architecture following banking industry security standards and DevSecOps best practices.

### 📋 Business Requirements

The system tracks device types (iOS, Android, Watch, TV) across multiple platforms, enabling the Business Team to identify the most frequently used device types through:

- **Public API** (Statistics API): Handles user login events and provides device statistics
- **Internal API** (Device Registration API): Manages device registration operations
- **Secure Database**: PostgreSQL for reliable data persistence

## 🏗️ Architecture

### Microservices Architecture

```
┌─────────────────┐    ┌──────────────────────┐    ┌─────────────────────┐
│   External      │    │   Statistics API     │    │  Device Registration│
│   Clients       │───►│  (Public Access)     │───►│  API (Internal)     │
│                 │    │   Port: 8080         │    │   Port: 8081        │
└─────────────────┘    └──────────────────────┘    └─────────────────────┘
                                │                             │
                                └─────────────┬───────────────┘
                                              │
                                    ┌─────────▼──────────┐
                                    │   PostgreSQL       │
                                    │   Database         │
                                    │   Port: 5432       │
                                    └────────────────────┘
```

### Key Components

1. **Statistics API** (`statistics-api/`) - **Port 8080**
   - **Purpose**: Public-facing API for login events and device statistics
   - **Endpoints**:
     - `POST /Log/auth` - Store user login event (calls Device Registration API)
     - `GET /Log/auth/statistics` - Retrieve device registration count by type
   - **Security**: Spring Security, input validation, rate limiting

2. **Device Registration API** (`device-registration-api/`) - **Port 8081**
   - **Purpose**: Internal API for device registration operations
   - **Endpoints**:
     - `POST /Device/register` - Register device type for a given user
   - **Security**: Internal-only access, service authentication headers

3. **Shared Models** (`shared-models/`)
   - **Purpose**: Common DTOs and entities
   - **Components**: Request/Response DTOs, JPA entities

4. **PostgreSQL Database**
   - **Purpose**: Persistent storage for device registrations
   - **Security**: Encrypted connections, user isolation

## 🔒 DevSecOps Security Features

### 🛡️ Application Security

- **Spring Security**: Enterprise-grade security framework
- **Input Validation**: Bean Validation with custom constraints
- **SQL Injection Prevention**: Parameterized queries, JPA
- **HTTPS Enforcement**: TLS/SSL in production
- **Security Headers**: HSTS, CSP, X-Frame-Options, etc.
- **Rate Limiting**: API throttling for DoS protection

### 🐳 Container Security

- **Multi-stage Builds**: Minimal attack surface
- **Non-root Users**: Containers run as non-privileged users
- **Read-only Filesystems**: Immutable container filesystems
- **Security Scanning**: Vulnerability assessment ready
- **Resource Limits**: CPU/Memory constraints

### ☸️ Kubernetes Security

- **Network Policies**: Micro-segmentation and zero-trust networking
- **RBAC**: Role-based access control
- **Pod Security Standards**: Restricted security contexts
- **Secrets Management**: Encrypted secret storage
- **Service Mesh Ready**: Istio/Linkerd integration prepared

### 📊 Observability & Monitoring

- **Health Checks**: Liveness, readiness, and startup probes
- **Metrics**: Prometheus metrics collection
- **Logging**: Structured logging with audit trails
- **Distributed Tracing**: OpenTelemetry ready
- **Grafana Dashboards**: Operational visibility

## 🏗️ CI/CD Pipeline

### GitHub Actions Workflow

The project includes a comprehensive CI/CD pipeline that automatically:

- **Detects Changes**: Uses path filters to determine which services need rebuilding
- **Runs Tests**: Executes Maven tests and security checks
- **Builds Images**: Creates Docker images only for changed services
- **Pushes to DockerHub**: Automatically publishes images with proper tags
- **Security Scanning**: Runs Trivy vulnerability scans on all images
- **DockerHub Integration**: Automated image publishing to DockerHub registry

### Image Naming Convention

```bash
# Statistics API
${DOCKERHUB_USERNAME}/safra-statistics-api:latest
${DOCKERHUB_USERNAME}/safra-statistics-api:main-<sha>

# Device Registration API
${DOCKERHUB_USERNAME}/safra-device-registration-api:latest
${DOCKERHUB_USERNAME}/safra-device-registration-api:main-<sha>
```

### Required GitHub Secrets

```bash
DOCKERHUB_USERNAME=your_dockerhub_username
DOCKERHUB_TOKEN=your_dockerhub_access_token
```

### Deployment Options

**Development (Local Build):**
```bash
docker-compose up -d --build
```

**Production (DockerHub Images):**
```bash
cp .env.production .env
docker-compose -f docker-compose.prod.yml up -d
```

## 🚀 Quick Start

### Prerequisites

- **Java 21** or later
- **Maven 3.9+**
- **Docker & Docker Compose**
- **Kubernetes cluster** (for K8s deployment)
- **Helm 3** (for Helm deployment)

### 🐳 Local Development with Docker Compose

1. **Clone and Build**
   ```bash
   git clone <repository-url>
   cd device-stats-devsecops-2025-09
   ```

2. **Start Services**
   ```bash
   # Start core services
   docker-compose up -d

   # Start with monitoring (optional)
   docker-compose --profile monitoring up -d
   ```

3. **Verify Deployment**
   ```bash
   # Check service health
   curl http://localhost:8080/actuator/health
   curl http://localhost:8081/actuator/health
   
   # Access API documentation
   open http://localhost:8080/swagger-ui.html
   open http://localhost:8081/swagger-ui.html
   ```

4. **Test the APIs**
   ```bash
   # Register a device via Statistics API (Public)
   curl -X POST http://localhost:8080/Log/auth \
     -H "Content-Type: application/json" \
     -d '{"userKey": "user123", "deviceType": "iOS"}'

   # Expected response: {"statusCode": 200, "message": "success"}

   # Get device statistics (Public)
   curl "http://localhost:8080/Log/auth/statistics?deviceType=iOS"

   # Expected response: {"deviceType": "iOS", "count": 5}

   # Test device registration directly (Internal API)
   curl -X POST http://localhost:8081/Device/register \
     -H "Content-Type: application/json" \
     -H "X-Internal-Service: statistics-api" \
     -d '{"userKey": "user456", "deviceType": "Android"}'

   # Expected response: {"statusCode": 200}
   ```

### ☸️ Kubernetes Deployment

#### Option 1: Raw Manifests

1. **Apply Kubernetes Manifests**
   ```bash
   # Create namespace
   kubectl apply -f k8s/manifests/namespace.yaml
   
   # Deploy database
   kubectl apply -f k8s/manifests/database/
   
   # Deploy APIs
   kubectl apply -f k8s/manifests/device-registration-api/
   kubectl apply -f k8s/manifests/statistics-api/
   
   # Apply network policies
   kubectl apply -f k8s/manifests/network-policies/
   ```

2. **Verify Deployment**
   ```bash
   kubectl get pods -n safra-device-stats
   kubectl get services -n safra-device-stats
   kubectl get ingress -n safra-device-stats
   ```

#### Option 2: Helm Chart (Recommended)

1. **Prerequisites**
   ```bash
   # Ensure you have a local Kubernetes cluster running
   kubectl cluster-info

   # Verify Helm is installed
   helm version
   ```

2. **Install the Helm Chart**
   ```bash
   # Download dependencies (PostgreSQL subchart)
   helm dependency update k8s/helm/safra-device-stats/

   # Install chart to local cluster
   helm install safra-device-stats k8s/helm/safra-device-stats/ \
     --namespace safra-device-stats \
     --create-namespace
   ```

3. **Verify Deployment**
   ```bash
   # Check Helm release status
   helm status safra-device-stats -n safra-device-stats

   # Monitor pod startup
   kubectl get pods -n safra-device-stats -w

   # Check services
   kubectl get services -n safra-device-stats
   ```

4. **Useful Helm Commands**
   ```bash
   # Upgrade deployment with new values
   helm upgrade safra-device-stats k8s/helm/safra-device-stats/ \
     --namespace safra-device-stats

   # View deployment history
   helm history safra-device-stats -n safra-device-stats

   # Rollback to previous version
   helm rollback safra-device-stats 1 -n safra-device-stats

   # Uninstall deployment
   helm uninstall safra-device-stats -n safra-device-stats

   # Delete namespace (optional)
   kubectl delete namespace safra-device-stats
   ```

5. **Customize Values for Different Environments**
   ```bash
   # Development with ingress disabled (default)
   helm install safra-device-stats k8s/helm/safra-device-stats/ \
     --namespace safra-device-stats \
     --create-namespace

   # Production with ingress enabled
   helm install safra-device-stats k8s/helm/safra-device-stats/ \
     --namespace safra-device-stats \
     --create-namespace \
     --set statisticsApi.ingress.enabled=true \
     --set statisticsApi.ingress.hosts[0].host=api.your-domain.com

   # Custom resource limits
   helm install safra-device-stats k8s/helm/safra-device-stats/ \
     --namespace safra-device-stats \
     --create-namespace \
     --set statisticsApi.resources.limits.memory=2Gi \
     --set deviceRegistrationApi.resources.limits.memory=1Gi
   ```

6. **Troubleshooting Helm Deployment**
   ```bash
   # View application logs
   kubectl logs -l app=statistics-api -n safra-device-stats
   kubectl logs -l app=device-registration-api -n safra-device-stats

   # Check PostgreSQL logs
   kubectl logs safra-device-stats-postgresql-0 -n safra-device-stats

   # Debug failed pods
   kubectl describe pod <pod-name> -n safra-device-stats

   # Port forward for local access
   kubectl port-forward svc/statistics-api 8080:8080 -n safra-device-stats
   kubectl port-forward svc/device-registration-api 8081:8081 -n safra-device-stats
   ```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | `safra_user` |
| `DB_PASSWORD` | Database password | `safra_password` |
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `5432` |
| `DEVICE_REGISTRATION_API_URL` | Internal API URL | `http://localhost:8081` |
| `JAVA_OPTS` | JVM options | `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0` |

### Profiles

- **default**: Local development
- **docker**: Docker Compose environment
- **production**: Production deployment with enhanced security

## 📊 API Documentation

### Statistics API (Public) - Port 8080

#### POST /Log/auth
Process user login event and register device.

**Request:**
```json
{
  "userKey": "string",
  "deviceType": "iOS|Android|Watch|TV"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "success"
}
```

#### GET /Log/auth/statistics
Retrieve device registration count by type.

**Parameters:**
- `deviceType`: Device type to query (iOS|Android|Watch|TV)

**Response:**
```json
{
  "deviceType": "iOS",
  "count": 42
}
```

### Device Registration API (Internal) - Port 8081

#### POST /Device/register
Register device for user (internal use only).

**Request:**
```json
{
  "userKey": "string",
  "deviceType": "iOS|Android|Watch|TV"
}
```

**Response:**
```json
{
  "statusCode": 200
}
```

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn test -Pintegration-tests
```

### Load Testing
```bash
# Using Apache Bench
ab -n 1000 -c 10 http://localhost:8080/Log/auth/statistics?deviceType=iOS
```

## 📈 Monitoring

### Access Monitoring Dashboards

1. **Prometheus**: http://localhost:9090 (with monitoring profile)
2. **Grafana**: http://localhost:3000 (admin/admin)
3. **Application Metrics**: 
   - Statistics API: http://localhost:8080/actuator/prometheus
   - Device Registration API: http://localhost:8081/actuator/prometheus

### Key Metrics

- **Request Rate**: HTTP requests per second
- **Response Time**: API response latencies
- **Error Rate**: HTTP 4xx/5xx error rates
- **Database Connections**: Active DB connections
- **JVM Metrics**: Memory, GC, threads

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow (Example)

```yaml
name: DevSecOps Pipeline
on: [push, pull_request]
jobs:
  security-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run Security Scan
        run: |
          mvn dependency-check:check
          docker run --rm -v $(pwd):/workspace aquasec/trivy filesystem /workspace
  
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run Tests
        run: mvn clean test
      - name: Build Docker Images
        run: |
          docker build -t safra-statistics-api:${{ github.sha }} -f statistics-api/Dockerfile .
          docker build -t safra-device-registration-api:${{ github.sha }} -f device-registration-api/Dockerfile .
```

## 🔐 Security Considerations

### Production Security Checklist

- [ ] Change default database passwords
- [ ] Configure TLS certificates
- [ ] Set up secret management (HashiCorp Vault, K8s Secrets)
- [ ] Configure network firewall rules
- [ ] Enable audit logging
- [ ] Set up intrusion detection
- [ ] Configure backup and disaster recovery
- [ ] Implement API authentication (JWT, OAuth2)
- [ ] Set up vulnerability scanning
- [ ] Configure SIEM integration

### Compliance

- **PCI DSS**: Payment card industry compliance ready
- **SOX**: Sarbanes-Oxley audit trail support
- **GDPR**: Data protection regulation compliance
- **Basel III**: Banking regulation compliance framework

## 🛠️ Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check database connectivity
   docker exec safra-postgres pg_isready -U safra_user -d safra_device_stats
   ```

2. **API Not Responding**
   ```bash
   # Check container logs
   docker logs safra-statistics-api
   docker logs safra-device-registration-api
   ```

3. **Service Health Check**
   ```bash
   # Verify all services are healthy
   docker-compose ps
   curl http://localhost:8080/actuator/health
   curl http://localhost:8081/actuator/health
   ```

## 🤝 Contributing

### Development Setup

1. **Fork the repository**
2. **Create feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit changes**: `git commit -m 'Add amazing feature'`
4. **Push to branch**: `git push origin feature/amazing-feature`
5. **Open Pull Request**

### Code Standards

- **Java**: Follow Google Java Style Guide
- **Security**: OWASP Top 10 compliance
- **Testing**: Minimum 80% code coverage
- **Documentation**: Comprehensive JavaDoc

## 📞 Support

### Interview Discussion Points

As a **Senior DevSecOps Engineer**, this solution demonstrates:

1. **Microservices Architecture**: Proper service decomposition and communication
2. **Security First**: Defense in depth with multiple security layers
3. **Container Security**: Secure containerization practices
4. **Infrastructure as Code**: Declarative Kubernetes manifests and Helm charts
5. **Observability**: Comprehensive monitoring and logging
6. **Automation Ready**: CI/CD pipeline integration
7. **Banking Standards**: Compliance with financial industry requirements

### Key Technical Decisions

- **Spring Boot**: Enterprise Java framework for banking reliability
- **PostgreSQL**: ACID compliance for financial data integrity
- **Kubernetes**: Container orchestration for scalability and security
- **Zero Trust Architecture**: Network policies for micro-segmentation
- **Immutable Infrastructure**: Container-based deployments

---

**Built with ❤️ for Safra Bank DevSecOps Excellence**

*This solution represents production-ready code following enterprise DevSecOps practices and banking industry security standards.*