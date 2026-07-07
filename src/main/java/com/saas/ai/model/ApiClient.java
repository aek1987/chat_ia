package com.saas.ai.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String apiKey;

    private String clientName;

    private String email;

    private Boolean active = true;

    private LocalDateTime createdAt;

    private Long requestCount = 0L;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
