package com.saas.ai.config;

import com.saas.ai.model.Order;
import com.saas.ai.model.Product;
import com.saas.ai.repository.OrderRepository;
import com.saas.ai.repository.ProductRepository;
import com.saas.ai.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            ProductRepository productRepo,
            OrderRepository orderRepo,
            ApiKeyService apiKeyService) {

        return args -> {
            log.info("=== Initialisation des données de démo ===");

            // Produits
            Product p1 = new Product(null, "Laptop Pro 15", "Ordinateur portable haute performance", 1299.99, 25, "Informatique", true);
            Product p2 = new Product(null, "Souris sans fil", "Souris ergonomique Bluetooth", 49.99, 100, "Accessoires", true);
            Product p3 = new Product(null, "Clavier mécanique", "Clavier gaming RGB", 89.99, 50, "Accessoires", true);
            productRepo.save(p1);
            productRepo.save(p2);
            productRepo.save(p3);

            // Commandes de démo
            Order o1 = new Order();
            o1.setCustomerId(123L);
            o1.setProductName("Laptop Pro 15");
            o1.setQuantity(1);
            o1.setTotalPrice(1299.99);
            o1.setStatus(Order.OrderStatus.SHIPPED);
            o1.setDeliveryAddress("12 rue de la Paix, Paris 75001");
            o1.setEstimatedDelivery(LocalDateTime.now().plusDays(1));
            orderRepo.save(o1);

            Order o2 = new Order();
            o2.setCustomerId(456L);
            o2.setProductName("Souris sans fil");
            o2.setQuantity(2);
            o2.setTotalPrice(99.98);
            o2.setStatus(Order.OrderStatus.PROCESSING);
            o2.setDeliveryAddress("5 avenue Victor Hugo, Lyon 69001");
            o2.setEstimatedDelivery(LocalDateTime.now().plusDays(2));
            orderRepo.save(o2);

            // Clé API de démo
            var demoClient = apiKeyService.createApiKey("Demo Client", "demo@saas.com");
            log.info("=== Clé API de démo créée : {} ===", demoClient.getApiKey());
            log.info("=== Utilisez cette clé dans le header: X-API-Key: {} ===", demoClient.getApiKey());
            log.info("=== H2 Console: http://localhost:8080/h2-console ===");
            log.info("=== API Health: http://localhost:8080/api/health ===");
        };
    }
}
