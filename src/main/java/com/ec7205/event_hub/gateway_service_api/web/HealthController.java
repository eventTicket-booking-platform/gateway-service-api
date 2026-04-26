package com.ec7205.event_hub.gateway_service_api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("ok");
    }
}
