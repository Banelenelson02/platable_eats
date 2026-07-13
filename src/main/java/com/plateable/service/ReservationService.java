package com.plateable.service;

import com.plateable.dto.request.CreateReservationRequest;
import com.plateable.dto.response.ReservationResponse;
import com.plateable.exception.ResourceNotFoundException;
import com.plateable.model.Reservation;
import com.plateable.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public List<ReservationResponse> getAllReservations() {
        return repository.findAll().stream().map(ReservationResponse::from).toList();
    }

    public ReservationResponse createReservation(CreateReservationRequest request) {
        String resId = "RES" + System.currentTimeMillis();
        Reservation reservation = new Reservation(resId, request.customerName(), request.tableId(), request.time(), request.partySize());
        return ReservationResponse.from(repository.save(reservation));
    }

    public void deleteReservation(String id) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation " + id + " not found"));
        repository.delete(reservation);
    }
}
