package com.plateable.service;

import com.plateable.dto.response.OrderResponse;
import com.plateable.exception.ResourceNotFoundException;
import com.plateable.model.MenuItem;
import com.plateable.model.Order;
import com.plateable.model.OrderStatus;
import com.plateable.repository.MenuItemRepository;
import com.plateable.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream().map(OrderResponse::from).toList();
    }

    public OrderResponse getOrder(String id) {
        return OrderResponse.from(findOrThrow(id));
    }

    public OrderResponse createOrder(String tableId, String waiterId) {
        String orderId = "ORD" + System.currentTimeMillis();
        Order order = new Order(orderId, tableId, waiterId);
        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderResponse addItemToOrder(String orderId, String menuItemId, int quantity, String instructions) {
        Order order = findOrThrow(orderId);
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item " + menuItemId + " not found"));
        
        if (!item.isAvailable()) {
            throw new IllegalArgumentException("Menu item " + menuItemId + " is currently unavailable");
        }
        
        order.addItem(item, quantity, instructions);
        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderResponse removeItemFromOrder(String orderId, String menuItemId) {
        Order order = findOrThrow(orderId);
        order.removeItem(menuItemId);
        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderResponse updateOrderStatus(String orderId, OrderStatus status) {
        Order order = findOrThrow(orderId);
        order.setStatus(status);
        return OrderResponse.from(orderRepository.save(order));
    }

    private Order findOrThrow(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + id + " not found"));
    }
}
