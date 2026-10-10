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

Event, Booking and Auth services enforce their business authorization. The
gateway permits their browser-facing API prefixes, with these explicit boundaries:

- `/user-service/api/v1/**`
- `/event-service/api/v1/**`
- `/booking-service/api/v1/**`
- notification administration requires an ADMIN realm role
- Event Service internal APIs and Booking Service's internal count route are denied
- the same rules apply under `/api/...`

Event/Booking ownership remains enforced downstream; the gateway does not infer
event ownership. Notification Service has no JWT middleware of its own, so its
direct backend port must remain private. Other notification routes still rely
on a trusted deployment boundary.

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
- `SecurityConfiguration` enforces the gateway boundaries above; downstream
  authorization remains authoritative for event ownership and booking access.
