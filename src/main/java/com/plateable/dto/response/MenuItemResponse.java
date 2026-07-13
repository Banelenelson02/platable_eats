package com.plateable.dto.response;

import com.plateable.model.MenuItem;

public record MenuItemResponse(String id, String name, double price, String category, boolean available) {
    public static MenuItemResponse from(MenuItem item) {
        return new MenuItemResponse(item.getId(), item.getName(), item.getPrice(),
                item.getCategory(), item.isAvailable());
    }
}
