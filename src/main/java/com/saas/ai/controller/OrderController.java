package com.saas.ai.controller;

import com.saas.ai.dto.Dtos.*;
import com.saas.ai.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // GET /api/orders
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) Long customerId) {
        if (customerId != null) {
            return ResponseEntity.ok(ApiResponse.ok(orderService.getOrdersByCustomer(customerId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAllOrders()));
    }

    // GET /api/orders/{id}
    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(o -> ResponseEntity.ok(ApiResponse.ok(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/orders
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(201).body(ApiResponse.ok(order));
    }

    // PUT /api/orders/{id}/status
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderService.updateStatus(id, status)
                .map(o -> ResponseEntity.ok(ApiResponse.ok(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/delivery/{orderId}
    @GetMapping("/delivery/{orderId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getDelivery(@PathVariable Long orderId) {
        DeliveryResponse delivery = orderService.getDeliveryStatus(orderId);
        return ResponseEntity.ok(ApiResponse.ok(delivery));
    }
}
