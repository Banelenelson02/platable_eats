package com.plateable.controller;

import com.plateable.dto.request.UpdateMenuItemRequest;
import com.plateable.dto.response.MenuItemResponse;
import com.plateable.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenu() {
        return ResponseEntity.ok(menuService.getMenu());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable String id) {
        return ResponseEntity.ok(menuService.getMenuItem(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable String id, 
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(menuService.update(id, request));
    }
}
