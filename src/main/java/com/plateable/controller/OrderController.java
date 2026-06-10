package com.plateable.controller;

import com.plateable.model.Order;
import com.plateable.model.OrderStatus;
import com.plateable.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final RestaurantService service;

    public OrderController(RestaurantService service) {
        this.service = service;
    }

    // GET http://localhost:8080/api/orders
    @GetMapping
    public List<Order> getAllOrders() {
        return service.getOrders();
    }

    // GET http://localhost:8080/api/orders?status=IN_KITCHEN
    @GetMapping(params = "status")
    public List<Order> getOrdersByStatus(@RequestParam OrderStatus status) {
        return service.getOrdersByStatus(status);
    }

    // GET http://localhost:8080/api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        return service.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST http://localhost:8080/api/orders
    // Body: { "tableId": "T2", "waiterId": "W001" }
    @PostMapping
    public Order createOrder(@RequestBody Map<String, String> body) {
        return service.createOrder(body.get("tableId"), body.get("waiterId"));
    }

    // POST http://localhost:8080/api/orders/{id}/items
    // Body: { "menuItemId": "M001", "quantity": 2, "instructions": "extra cheese" }
    @PostMapping("/{id}/items")
    public ResponseEntity<Order> addItem(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {

        String menuItemId  = (String) body.get("menuItemId");
        int quantity       = (Integer) body.get("quantity");
        String instructions = (String) body.getOrDefault("instructions", "");

        service.addItemToOrder(id, menuItemId, quantity, instructions);

        return service.getOrderById(id)
                .map(order -> ResponseEntity.ok(order))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE http://localhost:8080/api/orders/{id}/items/{menuItemId}
    @DeleteMapping("/{id}/items/{menuItemId}")
    public ResponseEntity<Order> removeItem(
            @PathVariable String id,
            @PathVariable String menuItemId) {
        service.removeItemFromOrder(id, menuItemId);
        return service.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH http://localhost:8080/api/orders/{id}/status?status=IN_KITCHEN
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status) {
        service.updateOrderStatus(id, status);
        return service.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}