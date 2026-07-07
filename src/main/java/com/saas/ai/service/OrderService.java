package com.saas.ai.service;

import com.saas.ai.dto.Dtos.*;
import com.saas.ai.model.Order;
import com.saas.ai.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id).map(this::toResponse);
    }

    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductName(request.getProductName());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(request.getTotalPrice());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setEstimatedDelivery(LocalDateTime.now().plusDays(3));
        return toResponse(orderRepository.save(order));
    }

    public Optional<OrderResponse> updateStatus(Long id, String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
            return toResponse(orderRepository.save(order));
        });
    }

    public DeliveryResponse getDeliveryStatus(Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            String message = switch (order.getStatus()) {
                case PENDING -> "Votre commande est en attente de confirmation.";
                case CONFIRMED -> "Votre commande a été confirmée et sera bientôt traitée.";
                case PROCESSING -> "Votre commande est en cours de préparation.";
                case SHIPPED -> "Votre commande est en livraison, arrivée prévue demain.";
                case DELIVERED -> "Votre commande a été livrée avec succès.";
                case CANCELLED -> "Votre commande a été annulée.";
            };

            return DeliveryResponse.builder()
                    .orderId(orderId)
                    .status(order.getStatus().name())
                    .trackingNumber("TRK-" + orderId + "-2024")
                    .estimatedDelivery(order.getEstimatedDelivery())
                    .currentLocation(order.getStatus() == Order.OrderStatus.SHIPPED ? "Centre de tri Paris" : null)
                    .message(message)
                    .build();
        }).orElse(DeliveryResponse.builder()
                .orderId(orderId)
                .status("NOT_FOUND")
                .message("Commande introuvable.")
                .build());
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .deliveryAddress(order.getDeliveryAddress())
                .createdAt(order.getCreatedAt())
                .estimatedDelivery(order.getEstimatedDelivery())
                .build();
    }
}
