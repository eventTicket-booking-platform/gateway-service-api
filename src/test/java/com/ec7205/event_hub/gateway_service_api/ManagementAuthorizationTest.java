package com.ec7205.event_hub.gateway_service_api;

import com.ec7205.event_hub.gateway_service_api.security.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.*;
import static org.mockito.Mockito.*;

@WebFluxTest(ManagementAuthorizationTest.ProbeController.class)
@Import({SecurityConfiguration.class, ManagementAuthorizationTest.ProbeController.class})
@ActiveProfiles("test")
class ManagementAuthorizationTest {
    @Autowired WebTestClient client;
    @MockBean ReactiveJwtDecoder decoder;

    @RestController
    static class ProbeController {
        @GetMapping({"/notification-service/api/v1/admin/notifications/stats",
                "/api/notification-service/api/v1/admin/notifications/stats",
                "/event-service/api/v1/internal/events/1/release",
                "/api/booking-service/api/v1/bookings/internal/event/1/count"})
        String ok() { return "ok"; }
    }
    @Test void notificationAdministrationRequiresAdminForBothGatewayPrefixes() {
        for (String prefix : List.of("", "/api")) {
            String path = prefix + "/notification-service/api/v1/admin/notifications/stats";
            client.get().uri(path).exchange().expectStatus().isUnauthorized();
            token("host", "HOST");
            client.get().uri(path).header("Authorization", "Bearer host").exchange().expectStatus().isForbidden();
            token("customer", "CUSTOMER");
            client.get().uri(path).header("Authorization", "Bearer customer").exchange().expectStatus().isForbidden();
            token("admin", "ADMIN");
            client.get().uri(path).header("Authorization", "Bearer admin").exchange().expectStatus().isOk();
        }
    }
    @Test void internalInventoryAndBookingCountsCannotBypassOwnershipViaGateway() {
        token("host", "HOST");
        for (String path : List.of("/event-service/api/v1/internal/events/1/release",
                "/api/booking-service/api/v1/bookings/internal/event/1/count")) {
            client.get().uri(path).header("Authorization", "Bearer host").exchange().expectStatus().isForbidden();
        }
    }
    private void token(String value, String role) {
        when(decoder.decode(value)).thenReturn(Mono.just(Jwt.withTokenValue(value).header("alg", "none")
                .subject(value).claim("realm_access", Map.of("roles", List.of(role))).build()));
    }
}
