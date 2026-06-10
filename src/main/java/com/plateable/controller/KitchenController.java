package com.plateable.controller;

import com.plateable.model.Order;
import com.plateable.model.OrderStatus;
import com.plateable.service.RestaurantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kitchen")
public class KitchenController {

    private final RestaurantService service;

    public KitchenController(RestaurantService service) {
        this.service = service;
    }

    // GET http://localhost:8080/api/kitchen
    // Returns { "inKitchen": [...], "ready": [...] }
    @GetMapping
    public Map<String, List<Order>> getKitchenQueue() {
        return Map.of(
                "inKitchen", service.getOrdersByStatus(OrderStatus.IN_KITCHEN),
                "ready",     service.getOrdersByStatus(OrderStatus.READY)
        );
    }

    // PATCH http://localhost:8080/api/kitchen/{orderId}/ready
    @PatchMapping("/{orderId}/ready")
    public Order markReady(@PathVariable String orderId) {
        service.updateOrderStatus(orderId, OrderStatus.READY);
        return service.getOrderById(orderId).orElseThrow();
    }
}