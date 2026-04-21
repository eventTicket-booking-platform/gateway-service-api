package com.ec7205.event_hub.gateway_service_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Value("${gateway.routes.event-service-uri}")
    private String eventServiceUri;

    @Value("${gateway.routes.auth-service-uri}")
    private String authServiceUri;

    @Value("${gateway.routes.booking-service-uri}")
    private String bookingServiceUri;

    @Value("${gateway.routes.notification-service-uri}")
    private String notificationServiceUri;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("event-service-api",
                        r -> r.path("/event-service/**")
                                .uri(eventServiceUri))
                .route("auth-service-api",
                        r -> r.path("/user-service/**")
                                .uri(authServiceUri))
                .route("booking-service-api",
                        r -> r.path("/booking-service/**")
                                .uri(bookingServiceUri))
                .route("notification-service-api",
                        r -> r.path("/notification-service/**")
                                .uri(notificationServiceUri))
                .build();
    }
}
