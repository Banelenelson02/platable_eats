package com.plateable.controller;

import com.plateable.model.Reservation;
import com.plateable.service.RestaurantService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final RestaurantService service;

    public ReservationController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return service.getReservations();
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Map<String, Object> payload) {
        String customerName = (String) payload.get("customerName");
        String tableId = (String) payload.get("tableId");
        int partySize = (Integer) payload.get("partySize");
        LocalDateTime time = payload.containsKey("time") ? LocalDateTime.parse(payload.get("time").toString()) : LocalDateTime.now();
        return service.createReservation(customerName, tableId, time, partySize);
    }

    @DeleteMapping("/{id}")
    public boolean cancelReservation(@PathVariable String id) {
        return service.cancelReservation(id);
    }
}