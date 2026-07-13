package com.plateable.model;

import jakarta.persistence.*;

@Entity
@jakarta.persistence.Table(name = "restaurant_tables")
public class Table {
    
    @Id
    private String tableId;
    private int capacity;
    
    @Enumerated(EnumType.STRING)
    private TableStatus status;
    
    private String currentOrderId;

    protected Table() {} // Required by JPA

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
