package com.plateable.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private MenuItem menuItem;

    private int quantity;
    private String specialInstructions;

    public OrderItem() {}

    public OrderItem(MenuItem menuItem, int quantity, String specialInstructions) {
        this.menuItem            = menuItem;
        this.quantity            = quantity;
        this.specialInstructions = specialInstructions;
    }

    public MenuItem getMenuItem()                          { return menuItem; }
    public void setMenuItem(MenuItem menuItem)             { this.menuItem = menuItem; }
    public int getQuantity()                               { return quantity; }
    public void setQuantity(int quantity)                  { this.quantity = quantity; }
    public String getSpecialInstructions()                 { return specialInstructions; }
    public void setSpecialInstructions(String instructions){ this.specialInstructions = instructions; }
    public double getSubtotal()                            { return menuItem.getPrice() * quantity; }
}