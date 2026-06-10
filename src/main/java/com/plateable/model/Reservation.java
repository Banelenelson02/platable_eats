package com.plateable.model;

import java.time.LocalDateTime;

public class Reservation {
    private String reservationId;
    private String customerName;
    private String tableId;
    private LocalDateTime reservationTime;
    private int partySize;
    private ReservationStatus status;

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