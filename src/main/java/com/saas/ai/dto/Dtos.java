package com.saas.ai.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// ================================
// Chat DTO
// ================================
public class Dtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        private String message;
        private Long customerId;
        private String sessionId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponse {
        private String reply;
        private String sessionId;
        private LocalDateTime timestamp;
        private String intent;
    }

    // ================================
    // Order DTOs
    // ================================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderRequest {
        private Long customerId;
        private String productName;
        private Integer quantity;
        private Double totalPrice;
        private String deliveryAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private Long id;
        private Long customerId;
        private String productName;
        private Integer quantity;
        private Double totalPrice;
        private String status;
        private String deliveryAddress;
        private LocalDateTime createdAt;
        private LocalDateTime estimatedDelivery;
    }

    // ================================
    // Delivery DTO
    // ================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryResponse {
        private Long orderId;
        private String status;
        private String trackingNumber;
        private LocalDateTime estimatedDelivery;
        private String currentLocation;
        private String message;
    }

    // ================================
    // Product DTO
    // ================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private Integer stock;
        private String category;
        private Boolean available;
    }

    // ================================
    // Generic API Response
    // ================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp;

        public static <T> ApiResponse<T> ok(T data) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}
