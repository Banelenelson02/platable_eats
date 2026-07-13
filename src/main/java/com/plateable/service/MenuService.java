package com.plateable.service;

import com.plateable.dto.request.UpdateMenuItemRequest;
import com.plateable.dto.response.MenuItemResponse;
import com.plateable.exception.ResourceNotFoundException;
import com.plateable.model.MenuItem;
import com.plateable.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuItemRepository repository;

    public MenuService(MenuItemRepository repository) {
        this.repository = repository;
    }

    public List<MenuItemResponse> getMenu() {
        return repository.findAll().stream().map(MenuItemResponse::from).toList();
    }

    public MenuItemResponse getMenuItem(String id) {
        return MenuItemResponse.from(findOrThrow(id));
    }

    public MenuItemResponse update(String id, UpdateMenuItemRequest request) {
        MenuItem item = findOrThrow(id);
        if (request.price() != null) item.setPrice(request.price());
        if (request.available() != null) item.setAvailable(request.available());
        return MenuItemResponse.from(repository.save(item));
    }

    private MenuItem findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item " + id + " not found"));
    }
}
