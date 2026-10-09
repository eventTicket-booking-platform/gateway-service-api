package com.ec7205.event_hub.gateway_service_api.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity){
        httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize->
                        authorize
                                .pathMatchers("/").permitAll()
                                .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                                // Inventory commands and internal counts are service-to-service,
                                // never browser APIs (including for authenticated hosts).
                                .pathMatchers("/event-service/api/v1/internal/**", "/api/event-service/api/v1/internal/**",
                                        "/booking-service/api/v1/bookings/internal/**", "/api/booking-service/api/v1/bookings/internal/**")
                                .denyAll()
                                .pathMatchers("/notification-service/api/v1/admin/**", "/api/notification-service/api/v1/admin/**")
                                .access((authentication, context) -> authentication.map(auth -> {
                                    boolean admin = false;
                                    if (auth.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
                                        java.util.Map<String, Object> realm = jwt.getClaim("realm_access");
                                        if (realm != null && realm.get("roles") instanceof java.util.Collection<?> roles) {
                                            admin = roles.stream().anyMatch(role -> "admin".equalsIgnoreCase(String.valueOf(role)));
                                        }
                                    }
                                    return new org.springframework.security.authorization.AuthorizationDecision(admin);
                                }).defaultIfEmpty(new org.springframework.security.authorization.AuthorizationDecision(false)))
                                .pathMatchers("/user-service/api/v1/**").permitAll()
                                .pathMatchers("/api/user-service/api/v1/**").permitAll()
                                .pathMatchers("/event-service/api/v1/**").permitAll()
                                .pathMatchers("/api/event-service/api/v1/**").permitAll()
                                .pathMatchers("/booking-service/api/v1/**").permitAll()
                                .pathMatchers("/api/booking-service/api/v1/**").permitAll()
                                .pathMatchers("/notification-service/api/v1/**").permitAll()
                                .pathMatchers("/api/notification-service/api/v1/**").permitAll()
                                .anyExchange().authenticated())
                        .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

        return httpSecurity.build();
    }

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(List.of("https://www.abc.com","https://abc.com"));
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setMaxAge(3600L);
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource basedCorsConfigurationSource =
                new UrlBasedCorsConfigurationSource();
        basedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(basedCorsConfigurationSource);
    }

}
