package com.plateable.controller;

import com.plateable.model.Table;
import com.plateable.service.RestaurantService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    private final RestaurantService service;

    public TableController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<Table> getAllTables() {
        return service.getTables();
    }

    @GetMapping("/available")
    public List<Table> getAvailableTables(@RequestParam(defaultValue = "1") int minCapacity) {
        return service.getAvailableTables(minCapacity);
    }

    @PostMapping
    public Table addTable(@RequestBody Map<String, Object> payload) {
        String id = (String) payload.get("tableId");
        int capacity = (Integer) payload.get("capacity");
        return service.addTable(id, capacity);
    }
}