package com.saas.ai.repository;

import com.saas.ai.model.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ApiClientRepository extends JpaRepository<ApiClient, Long> {
    Optional<ApiClient> findByApiKey(String apiKey);
    Optional<ApiClient> findByApiKeyAndActiveTrue(String apiKey);
}
