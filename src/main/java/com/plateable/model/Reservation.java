package com.plateable.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id
    private String reservationId;
    private String customerName;
    private String tableId;
    private LocalDateTime reservationTime;
    private int partySize;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {} // required by JPA

    public Reservation(String reservationId, String customerName,
                       String tableId, LocalDateTime time, int partySize) {
        this.reservationId   = reservationId;
        this.customerName    = customerName;
        this.tableId         = tableId;
        this.reservationTime = time;
        this.partySize       = partySize;
        this.status          = ReservationStatus.CONFIRMED;
    }

    public String getReservationId()               { return reservationId; }
    public String getCustomerName()                { return customerName; }
    public String getTableId()                     { return tableId; }
    public LocalDateTime getReservationTime()      { return reservationTime; }
    public int getPartySize()                      { return partySize; }
    public ReservationStatus getStatus()           { return status; }
    public void setStatus(ReservationStatus s)     { this.status = s; }
}