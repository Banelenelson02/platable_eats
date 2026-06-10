package com.plateable.service;

import com.plateable.model.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final Map<String, MenuItem> menu = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, Table> tables = new LinkedHashMap<>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<>();
    private final Map<String, String> employees = new LinkedHashMap<>(); // id -> name (simple fallback)

    private int orderCounter = 1;
    private int reservationCounter = 1;

    public RestaurantService() {
        // Initialize Menu
        menu.put("M001", new MenuItem("M001", "Wood-fired Pizza", 120.00, "Mains"));
        menu.put("M002", new MenuItem("M002", "Butternut Bisque", 85.00, "Mains"));
        menu.put("M003", new MenuItem("M003", "Beef Pot Pie", 145.00, "Mains"));
        menu.put("D001", new MenuItem("D001", "Malva Pudding", 65.00, "Desserts"));
        menu.put("D002", new MenuItem("D002", "Caramel Tart", 70.00, "Desserts"));

        // Initialize Tables
        tables.put("T1", new Table("T1", 2));
        tables.put("T2", new Table("T2", 4));
        tables.put("T3", new Table("T3", 6));
        tables.put("T4", new Table("T4", 4));
    }

    // --- MENU MANAGEMENT ---
    public List<MenuItem> getMenu() {
        return new ArrayList<>(menu.values());
    }

    public Optional<MenuItem> getMenuItemById(String id) {
        return Optional.ofNullable(menu.get(id));
    }

    public MenuItem addMenuItem(String id, String name, double price, String category) {
        MenuItem item = new MenuItem(id, name, price, category);
        menu.put(id, item);
        return item;
    }

    public void modifyMenuItem(String itemId, Double newPrice, Boolean available) {
        MenuItem item = menu.get(itemId);
        if (item != null) {
            if (newPrice != null) item.setPrice(newPrice);
            if (available != null) item.setAvailable(available);
        }
    }

    // --- TABLE MANAGEMENT ---
    public List<Table> getTables() {
        return new ArrayList<>(tables.values());
    }

    public Table addTable(String tableId, int capacity) {
        Table t = new Table(tableId, capacity);
        tables.put(tableId, t);
        return t;
    }

    public void modifyTable(String tableId, int newCapacity) {
        Table t = tables.get(tableId);
        if (t != null) {
            // Recreate the table configuration with the new capacity
            tables.put(tableId, new Table(tableId, newCapacity));
        }
    }

    public List<Table> getAvailableTables(int minCapacity) {
        return tables.values().stream()
                .filter(t -> t.getStatus() == TableStatus.AVAILABLE && t.getCapacity() >= minCapacity)
                .collect(Collectors.toList());
    }

    // --- ORDER MANAGEMENT ---
    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Optional<Order> getOrderById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public Order createOrder(String tableId, String waiterId) {
        String orderId = "ORD" + String.format("%03d", orderCounter++);
        Order order = new Order(orderId, tableId, waiterId);
        orders.put(orderId, order);

        Table t = tables.get(tableId);
        if (t != null) {
            t.setStatus(TableStatus.OCCUPIED);
            t.setCurrentOrderId(orderId);
        }
        return order;
    }

    public Order addItemToOrder(String orderId, String menuItemId, int qty, String instructions) {
        Order order = orders.get(orderId);
        MenuItem item = menu.get(menuItemId);
        if (order != null && item != null && item.isAvailable()) {
            order.addItem(item, qty, instructions);
        }
        return order;
    }

    public Order removeItemFromOrder(String orderId, String menuItemId) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.removeItem(menuItemId);
        }
        return order;
    }

    public Order updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
            if (status == OrderStatus.SERVED) {
                Table t = tables.get(order.getTableId());
                if (t != null) {
                    t.setStatus(TableStatus.AVAILABLE);
                    t.setCurrentOrderId(null);
                }
            }
        }
        return order;
    }

    // --- RESERVATION MANAGEMENT ---
    public List<Reservation> getReservations() {
        return new ArrayList<>(reservations.values());
    }

    public Reservation createReservation(String customerName, String tableId, LocalDateTime time, int partySize) {
        String resId = "RES" + String.format("%03d", reservationCounter++);
        Reservation res = new Reservation(resId, customerName, tableId, time, partySize);
        reservations.put(resId, res);
        Table t = tables.get(tableId);
        if (t != null) t.setStatus(TableStatus.RESERVED);
        return res;
    }

    public boolean cancelReservation(String reservationId) {
        Reservation res = reservations.get(reservationId);
        if (res != null && res.getStatus() == ReservationStatus.CONFIRMED) {
            res.setStatus(ReservationStatus.CANCELLED);
            Table t = tables.get(res.getTableId());
            if (t != null) t.setStatus(TableStatus.AVAILABLE);
            return true;
        }
        return false;
    }

    // --- EMPLOYEE MANAGEMENT ---
    public void addEmployee(String id, String name, String role) {
        employees.put(id, name + " (" + role + ")");
    }
}