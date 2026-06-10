package com.plateable.service;

import com.plateable.model.Employee;
import com.plateable.model.Reservation;
import com.plateable.model.Table;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Receptionist extends Employee {

    private final RestaurantService system;

    public Receptionist(RestaurantService system) {
        super("R001", "Emma Chen", "Receptionist");
        this.system = system;
    }

    public Table addTable(String tableId, int capacity) {
        return system.addTable(tableId, capacity);
    }

    public void modifyTableLayout(String tableId, int newCapacity) {
        system.modifyTable(tableId, newCapacity);
    }

    public Reservation createReservation(String customerName, String tableId,
                                         LocalDateTime time, int partySize) {
        return system.createReservation(customerName, tableId, time, partySize);
    }

    public boolean cancelReservation(String reservationId) {
        return system.cancelReservation(reservationId);
    }

    public List<Table> searchAvailableTables(int minCapacity) {
        return system.getAvailableTables(minCapacity);
    }
}
