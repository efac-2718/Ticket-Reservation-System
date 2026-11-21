package com.example.ticketreservation.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Document (collection = "reservations")
public class Reservation {

    @Id 
    private String id;
    @Indexed(unique = true)
    private String reservationCode;   // TKT-XXXXXXXX
    private String username;
    private String eventId;
    private int seats;
    private ReservationStatus status;
    private double amount;
}
