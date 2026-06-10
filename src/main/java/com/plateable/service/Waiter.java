package com.plateable.service;

import com.plateable.model.Employee;
import com.plateable.model.Order;
import com.plateable.model.OrderStatus;
import org.springframework.stereotype.Component;

@Component
public class Waiter extends Employee {

    private final RestaurantService system;

    public Waiter(RestaurantService system) {
        super("W001", "James Park", "Waiter");
        this.system = system;
    }

    public Order takeOrder(String tableId) {
        return system.createOrder(tableId, employeeId);
    }

    public void addItemToOrder(String orderId, String menuItemId,
                               int qty, String instructions) {
        system.addItemToOrder(orderId, menuItemId, qty, instructions);
    }

    public void submitToKitchen(String orderId) {
        system.updateOrderStatus(orderId, OrderStatus.IN_KITCHEN);
    }

    public void serveMealToTable(String orderId) {
        system.updateOrderStatus(orderId, OrderStatus.SERVED);
    }
}
