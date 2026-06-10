package com.plateable.model;

public abstract class Employee {
    protected String employeeId;
    protected String name;
    protected String role;

    public Employee(String employeeId, String name, String role) {
        this.employeeId = employeeId;
        this.name       = name;
        this.role       = role;
    }

    public String getEmployeeId() { return employeeId; }
    public String getName()       { return name; }
    public String getRole()       { return role; }
}