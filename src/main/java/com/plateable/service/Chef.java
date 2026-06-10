package com.plateable.service;

import com.plateable.model.Employee;
import com.plateable.model.Order;
import com.plateable.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Chef extends Employee {

    private final RestaurantService system;

    public Chef(RestaurantService system) {
        super("C001", "Marco Rossi", "Chef");
        this.system = system;
    }

    public List<Order> viewIncomingOrders() {
        return system.getOrdersByStatus(OrderStatus.IN_KITCHEN);
    }

    public void markOrderReady(String orderId) {
        system.updateOrderStatus(orderId, OrderStatus.READY);
    }
}