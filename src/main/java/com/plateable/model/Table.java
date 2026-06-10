package com.plateable.model;

public class Table {
    private String tableId;
    private int capacity;
    private TableStatus status;
    private String currentOrderId;

    public Table(String tableId, int capacity) {
        this.tableId  = tableId;
        this.capacity = capacity;
        this.status   = TableStatus.AVAILABLE;
    }

    public String getTableId()                        { return tableId; }
    public int getCapacity()                          { return capacity; }
    public TableStatus getStatus()                    { return status; }
    public void setStatus(TableStatus status)         { this.status = status; }
    public String getCurrentOrderId()                 { return currentOrderId; }
    public void setCurrentOrderId(String orderId)     { this.currentOrderId = orderId; }
}