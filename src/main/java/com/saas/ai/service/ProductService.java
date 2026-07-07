package com.saas.ai.service;

import com.saas.ai.dto.Dtos.*;
import com.saas.ai.model.Product;
import com.saas.ai.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByAvailableTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id).map(this::toResponse);
    }

    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(Product product) {
        return toResponse(productRepository.save(product));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .available(product.getAvailable())
                .build();
    }
}
