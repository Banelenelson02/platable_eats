package com.plateable.service;

import com.plateable.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class Manager extends Employee {

    private final RestaurantService system;

    public Manager(RestaurantService system) {
        super("M001", "Sofia Romano", "Manager");
        this.system = system;
    }

    public void addMenuItem(String id, String name, double price, String category) {
        system.addMenuItem(id, name, price, category);
    }

    public void modifyMenuItem(String itemId, Double newPrice, Boolean available) {
        system.modifyMenuItem(itemId, newPrice, available);
    }

    public void addWorker(String id, String workerName, String role) {
        system.addEmployee(id, workerName, role);
    }
}