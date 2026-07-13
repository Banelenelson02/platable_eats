package com.plateable.controller;

import com.plateable.dto.request.CreateTableRequest;
import com.plateable.dto.response.TableResponse;
import com.plateable.service.TableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<TableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/available")
    public ResponseEntity<List<TableResponse>> getAvailableTables(@RequestParam(defaultValue = "1") int minCapacity) {
        return ResponseEntity.ok(tableService.getAvailableTables(minCapacity));
    }

    @PostMapping
    public ResponseEntity<TableResponse> createTable(@Valid @RequestBody CreateTableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(request));
    }
}
