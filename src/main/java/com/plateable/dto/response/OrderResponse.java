package com.plateable.dto.response;

import com.plateable.model.Order;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderId, String tableId, String waiterId,
        String customerName, String contactNumber, String deliveryAddress, String specialInstructions,
        List<OrderItemResponse> items, String status,
        double total, LocalDateTime createdAt) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getOrderId(), order.getTableId(), order.getWaiterId(),
                order.getCustomerName(), order.getContactNumber(), order.getDeliveryAddress(), order.getSpecialInstructions(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getStatus().name(), order.getTotal(), order.getCreatedAt());
    }
}
