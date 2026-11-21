package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;

public record ReservationResponse(
    String id, String reservationCode, String eventID, String username,
    int seats, double amount, ReservationStatus status
) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(r.getId(), r.getReservationCode(), r.getEventId(),
                                        r.getUsername(), r.getSeats(), r.getAmount(), r.getStatus());
    }
}
