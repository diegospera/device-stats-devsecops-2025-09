# Nginx Reverse Proxy Setup

This configuration provides a production-ready nginx reverse proxy for the Safra Device Statistics APIs.

## Architecture

```
Internet → Nginx (Port 80/443) → Internal APIs
                ↓
    ┌─ /api/statistics/* → statistics-api:8080 (PUBLIC)
    └─ /api/internal/*   → device-registration-api:8081 (INTERNAL)
```

## API Access Patterns

### Public Statistics API
```bash
# Get device statistics (public access)
curl http://localhost/api/statistics/Log/auth/statistics?deviceType=mobile

# Authentication endpoint (public access)
curl -X POST http://localhost/api/statistics/Log/auth \
  -H "Content-Type: application/json" \
  -d '{"userKey": "user123", "deviceType": "mobile"}'
```

### Internal Device Registration API
```bash
# Register device (internal access only)
curl -X POST http://localhost/api/internal/Device/register \
  -H "Content-Type: application/json" \
  -H "X-Internal-Service: statistics-api" \
  -d '{"userKey": "user123", "deviceType": "mobile"}'
```

## Security Features

- **Rate Limiting**: 10 req/s for statistics API, 5 req/s for auth endpoints
- **Security Headers**: X-Frame-Options, X-Content-Type-Options, X-XSS-Protection
- **IP Restrictions**: Device Registration API can be restricted to internal networks
- **SSL Support**: Ready for HTTPS configuration in production

## Deployment

### Docker Compose
```bash
# Start with nginx proxy
docker-compose -f docker-compose.prod.yml up -d

# Check nginx status
curl http://localhost/health
```

### Production Considerations

1. **SSL Configuration**: Uncomment and configure HTTPS server block in `nginx/nginx.conf`
2. **Domain Setup**: Update server_name directive for your domain
3. **IP Whitelist**: Uncomment allow/deny directives for internal API access
4. **Monitoring**: Enable access logs for monitoring and analytics

## Troubleshooting

```bash
# Check nginx logs
docker logs safra-nginx

# Test internal connectivity
docker exec safra-nginx curl statistics-api:8080/actuator/health
docker exec safra-nginx curl device-registration-api:8081/actuator/health

# Validate nginx configuration
docker exec safra-nginx nginx -t
```