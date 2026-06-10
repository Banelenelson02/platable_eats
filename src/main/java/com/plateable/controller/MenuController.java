package com.plateable.controller;

import com.plateable.model.MenuItem;
import com.plateable.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final RestaurantService service;

    public MenuController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<MenuItem> getMenu() {
        return service.getMenu();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable String id) {
        return service.getMenuItemById(id)
                .map(item -> ResponseEntity.ok(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> modifyMenuItem(
            @PathVariable String id,
            @RequestBody Map<String, Object> payload) {
        Double price = payload.containsKey("price") ? Double.valueOf(payload.get("price").toString()) : null;
        Boolean available = payload.containsKey("available") ? (Boolean) payload.get("available") : null;
        service.modifyMenuItem(id, price, available);
        return ResponseEntity.ok().build();
    }
}