package com.plateable.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String tableId;
    private String waiterId;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public Order(String orderId, String tableId, String waiterId) {
        this.orderId   = orderId;
        this.tableId   = tableId;
        this.waiterId  = waiterId;
        this.items     = new ArrayList<>();
        this.status    = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getOrderId()              { return orderId; }
    public String getTableId()              { return tableId; }
    public String getWaiterId()             { return waiterId; }
    public List<OrderItem> getItems()       { return items; }
    public OrderStatus getStatus()          { return status; }
    public void setStatus(OrderStatus s)    { this.status = s; }
    public LocalDateTime getCreatedAt()     { return createdAt; }

    public void addItem(MenuItem item, int qty, String instructions) {
        for (OrderItem oi : items) {
            if (oi.getMenuItem().getId().equals(item.getId())) {
                oi.setQuantity(oi.getQuantity() + qty);
                return;
            }
        }
        items.add(new OrderItem(item, qty, instructions));
    }

    public boolean removeItem(String menuItemId) {
        return items.removeIf(oi -> oi.getMenuItem().getId().equals(menuItemId));
    }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }
}