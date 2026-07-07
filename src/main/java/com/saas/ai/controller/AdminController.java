package com.saas.ai.controller;

import com.saas.ai.model.ApiClient;
import com.saas.ai.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final ApiKeyService apiKeyService;

    // Health check public
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "SaaS AI Backend",
                "version", "1.0.0"
        ));
    }

    // Créer un nouveau client avec sa clé API
    @PostMapping("/admin/clients")
    public ResponseEntity<ApiClient> createClient(
            @RequestParam String clientName,
            @RequestParam String email) {
        ApiClient client = apiKeyService.createApiKey(clientName, email);
        return ResponseEntity.status(201).body(client);
    }

    // Vérifier une clé API
    @GetMapping("/admin/clients/validate")
    public ResponseEntity<Map<String, Object>> validateKey(@RequestParam String apiKey) {
        boolean valid = apiKeyService.validateApiKey(apiKey);
        return ResponseEntity.ok(Map.of(
                "valid", valid,
                "apiKey", apiKey
        ));
    }
}
