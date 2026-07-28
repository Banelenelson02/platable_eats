package com.plateable.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    private String id;
    private String name;
    private double price;
    private String category;
    private boolean available;

    protected MenuItem() {} // required by JPA

    public MenuItem(String id, String name, double price, String category) {
        this.id        = id;
        this.name      = name;
        this.price     = price;
        this.category  = category;
        this.available = true;
    }

    public String getId()                              { return id; }
    public String getName()                            { return name; }
    public double getPrice()                           { return price; }
    public String getCategory()                        { return category; }
    public boolean isAvailable()                       { return available; }
    public void setAvailable(boolean available)        { this.available = available; }
    public void setPrice(double price)                 { this.price = price; }
}