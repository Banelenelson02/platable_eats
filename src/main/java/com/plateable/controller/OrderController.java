package com.plateable.controller;

import com.plateable.dto.request.AddOrderItemRequest;
import com.plateable.dto.request.CreateOrderRequest;
import com.plateable.dto.response.OrderResponse;
import com.plateable.model.OrderStatus;
import com.plateable.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestParam(required = false) OrderStatus status) {
        if (status != null) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(status));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request.tableId(), request.waiterId()));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addItem(
            @PathVariable String id, 
            @Valid @RequestBody AddOrderItemRequest request) {
        return ResponseEntity.ok(orderService.addItemToOrder(id, request.menuItemId(), request.quantity(), request.instructions()));
    }

    @DeleteMapping("/{id}/items/{menuItemId}")
    public ResponseEntity<OrderResponse> removeItem(@PathVariable String id, @PathVariable String menuItemId) {
        return ResponseEntity.ok(orderService.removeItemFromOrder(id, menuItemId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable String id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
