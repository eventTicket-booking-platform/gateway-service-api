# Gateway Service API

API gateway for Event Hub. This service fronts the backend APIs, exposes stable route prefixes, rewrites `/api/...` paths for browser clients, and forwards requests to downstream services.

## Stack

- Java 17
- Spring Boot 3
- Spring Cloud Gateway
- Spring Security WebFlux
- Spring Cloud Config client
- Eureka client

## Functional Requirements

- Route auth traffic to `auth-service-api`
- Route event traffic to `event-service-api`
- Route booking traffic to `booking-service-api`
- Route notification traffic to `notification-service-api`
- Support both direct service paths and browser-facing `/api/...` paths
- Rewrite `/api/<segment>` to `/<segment>` before forwarding
- Expose permissive CORS for frontend access

## Non-Functional Requirements

- Centralized configuration through Config Server
- Eureka registration
- Stateless JWT resource server support
- Reactive routing with Spring Cloud Gateway
- Health endpoint through Actuator

## Routed Paths

- `/user-service/**` and `/api/user-service/**` -> auth service
- `/event-service/**` and `/api/event-service/**` -> event service
- `/booking-service/**` and `/api/booking-service/**` -> booking service
- `/notification-service/**` and `/api/notification-service/**` -> notification service

## Role-Based Access

Current gateway security is permissive for all backend service API prefixes:

- `/user-service/api/v1/**`
- `/event-service/api/v1/**`
- `/booking-service/api/v1/**`
- `/notification-service/api/v1/**`
- same paths under `/api/...`

That means the gateway currently does not enforce RBAC on business endpoints. Authorization is delegated to downstream services, and for notification routes not even that exists yet.

## Runtime Dependencies

- Config Server on `8888`
- Eureka Server on `8761`
- Auth service
- Event service
- Booking service
- Notification service

## Local Setup

1. Ensure Config Server and Eureka Server are up.
2. Ensure all downstream services are reachable.
3. Set required config values through Config Server or environment:
   - `SPRING_CLOUD_CONFIG_URI`
   - `GATEWAY_ROUTES_EVENT_SERVICE_URI`
   - `GATEWAY_ROUTES_AUTH_SERVICE_URI`
   - `GATEWAY_ROUTES_BOOKING_SERVICE_URI`
   - `GATEWAY_ROUTES_NOTIFICATION_SERVICE_URI`
4. Run:

```powershell
.\mvnw.cmd spring-boot:run
```

Default port: `9090`

## Build

```powershell
.\mvnw.cmd clean package
```

## Notes

- Frontends in this project assume the gateway is reachable at `/api`.
- If you want the gateway itself to own access control, tighten `SecurityConfiguration` instead of relying only on downstream services.
