package com.saas.ai.service;

import com.saas.ai.model.ApiClient;
import com.saas.ai.repository.ApiClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyService {

    private final ApiClientRepository apiClientRepository;

    public boolean validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) return false;

        Optional<ApiClient> client = apiClientRepository.findByApiKeyAndActiveTrue(apiKey);
        client.ifPresent(c -> {
            c.setRequestCount(c.getRequestCount() + 1);
            apiClientRepository.save(c);
            log.debug("API Key validée pour client: {}", c.getClientName());
        });

        return client.isPresent();
    }

    public ApiClient createApiKey(String clientName, String email) {
        ApiClient client = new ApiClient();
        client.setClientName(clientName);
        client.setEmail(email);
        client.setApiKey(generateApiKey(clientName));
        client.setActive(true);
        client.setRequestCount(0L);
        return apiClientRepository.save(client);
    }

    public Optional<ApiClient> getClientByApiKey(String apiKey) {
        return apiClientRepository.findByApiKey(apiKey);
    }

    private String generateApiKey(String clientName) {
        String prefix = clientName.toUpperCase().replaceAll("[^A-Z0-9]", "").substring(0, Math.min(3, clientName.length()));
        return "CLIENT_" + prefix + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
