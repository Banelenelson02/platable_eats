package com.plateable.dto.response;

import com.plateable.model.OrderItem;

public record OrderItemResponse(String menuItemId, String name, int quantity, String instructions, double subtotal) {
    public static OrderItemResponse from(OrderItem oi) {
        return new OrderItemResponse(oi.getMenuItem().getId(), oi.getMenuItem().getName(),
                oi.getQuantity(), oi.getSpecialInstructions(), oi.getSubtotal());
    }
}
