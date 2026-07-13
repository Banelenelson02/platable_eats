package com.plateable.dto.response;

import com.plateable.model.Reservation;
import java.time.LocalDateTime;

public record ReservationResponse(String reservationId, String customerName, String tableId, LocalDateTime time, int partySize, String status) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getReservationId(), reservation.getCustomerName(),
                reservation.getTableId(), reservation.getReservationTime(), reservation.getPartySize(), reservation.getStatus().name());
    }
}
